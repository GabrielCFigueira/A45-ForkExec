package com.forkexec.pts.ws.cli;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import com.forkexec.pts.ws.cli.exception.*;
import com.forkexec.pts.ws.*;

/**
 * A class to implement QC protocol using the PointsClient for communication
 * with the server
 */
public class PointsFrontEnd {
	
	private static int nSERVERS;
	private static int majority;
	
	public PointsFrontEnd(int num){
		nSERVERS = num;
		majority = (num / 2) + 1;
	}

	public int pointsBalance(String UDDIUrl , List<String> orgNames, String userEmail) throws InvalidEmailAddressException, EmailIsNotRegisteredException {
		return read(UDDIUrl, orgNames, userEmail).getPoints();
	}

	public int addPoints(String UDDIUrl , List<String> orgNames, String userEmail, int pointsToAdd)
			throws InvalidEmailAddressException, InvalidNumberOfPointsException, EmailIsNotRegisteredException {
		
		TaggedBalance balance = read(UDDIUrl, orgNames, userEmail);
		
		balance.setPoints(pointsToAdd+balance.getPoints());
		balance.setTag(balance.getTag() + 1);
		
		write(UDDIUrl, orgNames, userEmail, balance);

		return balance.getPoints();
	}

	public int spendPoints(String UDDIUrl, List<String> orgNames, String userEmail, int pointsToSpend)
			throws InvalidEmailAddressException, InvalidNumberOfPointsException, EmailIsNotRegisteredException,
			NotEnoughPointsException {
		
		TaggedBalance balance = read(UDDIUrl, orgNames, userEmail);
		
		balance.setPoints(balance.getPoints()-pointsToSpend);
		balance.setTag(balance.getTag() + 1);
		
		write(UDDIUrl, orgNames, userEmail, balance);

		return balance.getPoints();
	}

	// control operations -----------------------------------------------------

	public String ctrlPing(String UDDIUrl , List<String> orgNames, String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append("Hub");

		for(String orgName : orgNames)
			builder.append("\n").append(getPointsClient(UDDIUrl, orgName).ctrlPing("points client"));

		return builder.toString();
	}

	public void ctrlClear(String UDDIUrl , List<String> orgNames) {
		for(String orgName : orgNames)
			getPointsClient(UDDIUrl, orgName).ctrlClear();
	}

	public void ctrlInit(String UDDIUrl , List<String> orgNames, int startPoints) throws BadInitFault_Exception {
		for(String orgName : orgNames)
			getPointsClient(UDDIUrl, orgName).ctrlInit(startPoints);
	}

	private PointsClient getPointsClient(String UDDIUrl, String orgName) {
		try {
			return new PointsClient(UDDIUrl, orgName);
		} catch(PointsClientException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private List<PointsClient> getPointsClients(String UDDIUrl, List<String> orgNames) {
		List<PointsClient> res = new ArrayList<PointsClient>();

		for(String orgName : orgNames)
			res.add(getPointsClient(UDDIUrl, orgName));
		
		return res;
	}
	
	// Aux --------------------------------------------------------------------
	
	private TaggedBalance read(String UDDIUrl, List<String> orgNames, String userEmail) {
		
		List<PointsClient> clients = getPointsClients(UDDIUrl, orgNames);

		List<Response<GetBalanceResponse>> responses = new ArrayList<Response<GetBalanceResponse>>();
		for(PointsClient client : clients)
			responses.add(client.getBalanceAsync(userEmail));

		int nResponses = 0;
		TaggedBalance balance = new TaggedBalance();
		balance.setTag(-1);
		while(nResponses < majority) {
			for(int i=responses.size()-1; i >= 0; --i) {
				if(responses.get(i).isDone()) {
					TaggedBalance newTag = new TaggedBalance();
					newTag.setTag(-1);
					try {
						newTag = responses.get(i).get().getReturn();
						nResponses++;
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
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
	
	private boolean write(String UDDIUrl, List<String> orgNames, String userEmail, TaggedBalance balance) {
		List<PointsClient> clients = getPointsClients(UDDIUrl, orgNames);

		List<Response<SetBalanceResponse>> responses = new ArrayList<Response<SetBalanceResponse>>();
				
		for(PointsClient client : clients)
			responses.add(client.setBalanceAsync(userEmail, balance));
		
		int nResponses = 0;
		while(nResponses < majority) {
			for(int i=responses.size()-1; i >= 0; --i) {
				if(responses.get(i).isDone()) {
					try {
						responses.get(i).get();
						nResponses++;
					} catch (InterruptedException | ExecutionException e) {
						// TODO Auto-generated catch block
					}
					responses.remove(i);
				}
			}
		}
		
		return true;
	}

}
