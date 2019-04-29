package com.forkexec.pts.ws.cli;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;


import com.forkexec.pts.ws.*;

/**
 * A class to implement QC protocol using the PointsClient
 * for communication with the server
 */
public class PointsFrontEnd {

	public int pointsBalance(String UDDIUrl , List<String> orgNames, String userEmail) throws InvalidEmailFault_Exception {
		List<PointsClient> clients = getPointsClients(UDDIUrl, orgNames);

		List<Response<GetBalanceResponse>> responses = new ArrayList<Response<GetBalanceResponse>>();
		for(PointsClient client : clients)
			responses.add(client.getBalanceAsync(userEmail));

		int nServers = clients.size();
		int majority = nServers / 2 + 1;
		int nResponses = 0;
		//while(nResponses < majority) 

		return 0;
	}

	public int addPoints(String UDDIUrl , List<String> orgNames, String userEmail, int pointsToAdd)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		
		List<PointsClient> clients = getPointsClients(UDDIUrl, orgNames);

		List<Response<GetBalanceResponse>> responses = new ArrayList<Response<GetBalanceResponse>>();
		for(PointsClient client : clients)
			responses.add(client.getBalanceAsync(userEmail));

		return 0;
	}

	public int spendPoints(String UDDIUrl , List<String> orgNames, String userEmail, int pointsToSpend)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception /*, NotEnoughBalanceFault_Exception*/ {
		
		List<PointsClient> clients = getPointsClients(UDDIUrl, orgNames);

		List<Response<GetBalanceResponse>> responses = new ArrayList<Response<GetBalanceResponse>>();
		for(PointsClient client : clients)
			responses.add(client.getBalanceAsync(userEmail));

		return 0;
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

}
