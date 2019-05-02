package com.forkexec.pts.ws.cli;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import com.forkexec.pts.ws.cli.exception.*;
import com.forkexec.pts.ws.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.*;

/**
 * A class to implement QC protocol using the PointsClient for communication
 * with the server
 */
public class PointsFrontEnd {

	private PointsFrontEndEndPointManager endpoint;

	private static int nSERVERS;
	private static int majority;

	public PointsFrontEnd(int num, String UDDIUrl) {
		nSERVERS = num;
		majority = (num / 2) + 1;
		try {
			endpoint = new PointsFrontEndEndPointManager(UDDIUrl);
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public int pointsBalance(String userEmail) throws InvalidEmailAddressException, EmailIsNotRegisteredException {
		return read(userEmail).getPoints();
	}

	public int addPoints(String userEmail, int pointsToAdd)
			throws InvalidEmailAddressException, InvalidNumberOfPointsException, EmailIsNotRegisteredException {

		TaggedBalance balance = read(userEmail);

		balance.setPoints(pointsToAdd + balance.getPoints());
		balance.setTag(balance.getTag() + 1);

		write(userEmail, balance);

		return balance.getPoints();
	}

	public int spendPoints(String userEmail, int pointsToSpend) throws InvalidEmailAddressException,
			InvalidNumberOfPointsException, EmailIsNotRegisteredException, NotEnoughPointsException {

		TaggedBalance balance = read(userEmail);
		if (pointsToSpend < 0)
			throw new InvalidNumberOfPointsException(pointsToSpend);
		if (balance.getPoints() - pointsToSpend < 0)
			throw new NotEnoughPointsException(pointsToSpend, balance.getPoints());

		balance.setPoints(balance.getPoints() - pointsToSpend);
		balance.setTag(balance.getTag() + 1);

		write(userEmail, balance);

		return balance.getPoints();
	}

	// control operations -----------------------------------------------------

	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append("Hub");

		// FIXME try catch
		for (PointsClient client : getPointsClients()) {
			try {
				builder.append("\n").append(client.ctrlPing("points client"));
			} catch (RuntimeException e) {
				/* move on */
			}
		}
		return builder.toString();
	}

	public void ctrlClear() {
		for (PointsClient client : getPointsClients()) {
			try {
				client.ctrlClear();
			} catch (RuntimeException e) {
				/* move on */
			}
		}
	}

	public void ctrlInit(int startPoints) throws BadInitFault_Exception {
		for (PointsClient client : getPointsClients()) {
			try {
				client.ctrlInit(startPoints);
			} catch (RuntimeException e) {
				/* move on */
			}
		}
	}

	public void ctrlEnable(int server, int delay) {
		try {
			PointsClient p = new PointsClient(endpoint.getUDDIUrl(), String.format("A45_Points%d", server));
			p.ctrlEnable(delay);
		} catch (PointsClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<PointsClient> getPointsClients() {
		List<PointsClient> res = new ArrayList<PointsClient>();

		try {
			for(UDDIRecord r: endpoint.listRecords("A45_Points%")) {
				try {
					PointsClient p = new PointsClient(endpoint.getUDDIUrl(), r.getOrgName());
					res.add(p);
				} catch (PointsClientException e) {
					/* let qc take care of this*/
				}
			}
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return res;
	}
	
	// Aux --------------------------------------------------------------------
	
	private TaggedBalance read(String userEmail) throws InvalidEmailAddressException {
		
		List<PointsClient> clients = getPointsClients();

		List<Response<GetBalanceResponse>> responses = new ArrayList<Response<GetBalanceResponse>>();
		for(PointsClient client : clients)
			responses.add(client.getBalanceAsync(userEmail));

		int nResponses = 0;
		TaggedBalance balance = new TaggedBalance();
		balance.setTag(-1);
		while(nResponses < majority) {
			if(responses.size() == 0) break; //TODO error
			for(int i=responses.size()-1; i >= 0; --i) {
				if(responses.get(i).isDone()) {
					TaggedBalance newTag = new TaggedBalance();
					newTag.setTag(-1);
					try {
						newTag = responses.get(i).get().getReturn();
						nResponses++;
					} catch (InterruptedException e) {
						
					} catch (ExecutionException e) {
						if(e.getCause() instanceof InvalidEmailFault_Exception)
							throw new InvalidEmailAddressException(userEmail);
					}
					if(newTag.getTag() > balance.getTag()) {
						balance = newTag;
					}
					
					responses.remove(i);
				}
			}
		}
		
		return balance;
		
	}
	
	private boolean write(String userEmail, TaggedBalance balance) throws InvalidEmailAddressException, InvalidNumberOfPointsException {
		List<PointsClient> clients = getPointsClients();

		List<Response<SetBalanceResponse>> responses = new ArrayList<Response<SetBalanceResponse>>();
				
		for(PointsClient client : clients)
			responses.add(client.setBalanceAsync(userEmail, balance));
		
		int nResponses = 0;
		while(nResponses < majority) {
			if(responses.size() == 0) break; //TODO error
			for(int i=responses.size()-1; i >= 0; --i) {
				if(responses.get(i).isDone()) {
					try {
						responses.get(i).get();
						nResponses++;
					} catch (InterruptedException e) {
						throw new RuntimeException(e.getMessage());
					} catch (ExecutionException e) {
						if(e.getCause() instanceof InvalidEmailFault_Exception)
							throw new InvalidEmailAddressException(userEmail);
						else if(e.getCause() instanceof InvalidPointsFault_Exception)
							throw new InvalidNumberOfPointsException(balance.getPoints());
					}
					responses.remove(i);
				}
			}
		}
		
		return true;
	}

}
