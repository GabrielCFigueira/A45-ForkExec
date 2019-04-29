package com.forkexec.pts.ws.cli;

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

	// TODO: Add list of PointsClients here
	// TODO: Implement QC in these methods
	public int pointsBalance(String userEmail) throws InvalidEmailFault_Exception {
		return 0;
	}

	public int addPoints(String userEmail, int pointsToAdd)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		return 0;
	}

	public int spendPoints(String userEmail, int pointsToSpend)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception /*, NotEnoughBalanceFault_Exception*/ {
		return 0;
	}

	// control operations -----------------------------------------------------

	public String ctrlPing(String inputMessage) {
		return "";
	}

	public void ctrlClear() {
	}

	public void ctrlInit(int startPoints) throws BadInitFault_Exception {
	}

}
