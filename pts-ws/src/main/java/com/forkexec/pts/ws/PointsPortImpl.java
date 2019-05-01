package com.forkexec.pts.ws;

import javax.jws.WebService;

import com.forkexec.pts.domain.Points;
import com.forkexec.pts.domain.Balance;
import com.forkexec.pts.domain.exception.*;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.pts.ws.PointsPortType", wsdlLocation = "PointsService.wsdl", name = "PointsWebService", portName = "PointsPort", targetNamespace = "http://ws.pts.forkexec.com/", serviceName = "PointsService")
public class PointsPortImpl implements PointsPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private final PointsEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public PointsPortImpl(final PointsEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------

	// QC-supporting methods
	@Override
	public TaggedBalance getBalance(String userEmail) throws InvalidEmailFault_Exception {
		TaggedBalance t = null;
		try {
			t = balanceToTaggedBalance(Points.getInstance().getBalance(userEmail));
		} catch (InvalidEmailAddressException e) {
			throwInvalidEmailFault(e.getMessage());
		}
		return t;
	}

	@Override
	public void setBalance(String userEmail, TaggedBalance taggedBalance)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		try {
			Points.getInstance().setBalance(userEmail, taggedBalance.getPoints(), taggedBalance.getTag());
		} catch (InvalidEmailAddressException e) {
			throwInvalidEmailFault(e.getMessage());
		} catch (InvalidNumberOfPointsException e) {
			throwInvalidPointsFault(e.getMessage());
		}
	}

	// Control operations ----------------------------------------------------
	/** Diagnostic operation to check if service is running. */
	@Override
	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the park does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Park";

		// Build a string with a message to return.
		final StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}

	/** Return all variables to default values. */
	@Override
	public void ctrlClear() {
		Points.resetInstance();
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInit(final int startPoints) throws BadInitFault_Exception {
		try {
			Points.changeStartPoints(startPoints);
		} catch (InvalidNumberOfPointsException e) {
			throwBadInit(e.getMessage());
		}
	}

	// Exception helpers -----------------------------------------------------

	/** Helper to throw a new BadInit exception. */
	private void throwBadInit(final String message) throws BadInitFault_Exception {
		final BadInitFault faultInfo = new BadInitFault();
		faultInfo.message = message;
		throw new BadInitFault_Exception(message, faultInfo);
	}

	/** Helper to throw a new InvalidEmailFault exception. */
	private void throwInvalidEmailFault(final String message) throws InvalidEmailFault_Exception {
		final InvalidEmailFault faultInfo = new InvalidEmailFault();
		faultInfo.message = message;
		throw new InvalidEmailFault_Exception(message, faultInfo);
	}

	/** Helper to throw a new InvalidPointsFault exception. */
	private void throwInvalidPointsFault(final String message) throws InvalidPointsFault_Exception {
		final InvalidPointsFault faultInfo = new InvalidPointsFault();
		faultInfo.message = message;
		throw new InvalidPointsFault_Exception(message, faultInfo);
	}

	private TaggedBalance balanceToTaggedBalance(Balance b) {
		TaggedBalance res = new TaggedBalance();
		res.setPoints(b.getPoints());
		res.setTag(b.getSeq());
		return res;
	}
}