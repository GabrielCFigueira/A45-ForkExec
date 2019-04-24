package com.forkexec.pts.ws;

import java.util.concurrent.Future;

import javax.jws.WebService;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import com.forkexec.pts.domain.Points;
import com.forkexec.pts.domain.exception.EmailAlreadyRegisteredException;
import com.forkexec.pts.domain.exception.EmailIsNotRegisteredException;
import com.forkexec.pts.domain.exception.InvalidEmailAddressException;
import com.forkexec.pts.domain.exception.InvalidNumberOfPointsException;
import com.forkexec.pts.domain.exception.NotEnoughPoints;

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

	@Override
	public void activateUser(final String userEmail)
			throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		try {
			Points.getInstance().addUser(userEmail);
		} catch (EmailAlreadyRegisteredException e) {
			throwEmailAlreadyExists(e.getMessage());
		} catch (InvalidEmailAddressException e) {
			throwInvalidEmailFault(e.getMessage());
		}
	}

	@Override
	public int pointsBalance(final String userEmail) throws InvalidEmailFault_Exception {
		int res = -1;
		try {
			res = Points.getInstance().getPoints(userEmail);
		} catch (EmailIsNotRegisteredException | InvalidEmailAddressException e) {
			throwInvalidEmailFault(e.getMessage());
		}
		return res;
	}

	@Override
	public int addPoints(final String userEmail, final int pointsToAdd)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		int res = -1;
		if (pointsToAdd <= 0) {
			throwInvalidPointsFault(String.format("Cannot add '%d' points (cannot be a negative number)", pointsToAdd));
		}
		try {
			res = Points.getInstance().changePoints(userEmail, pointsToAdd);
		} catch (EmailIsNotRegisteredException | InvalidEmailAddressException e) {
			throwInvalidEmailFault(e.getMessage());
		} catch (NotEnoughPoints e) {
			throwInvalidPointsFault(e.getMessage());
		}

		return res;
	}

	@Override
	public int spendPoints(final String userEmail, final int pointsToSpend)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
		int res = -1;

		if (pointsToSpend <= 0) {
			throwInvalidPointsFault(
					String.format("Cannot spend '%d' points (cannot be a negative number)", pointsToSpend));
		}

		try {
			res = Points.getInstance().changePoints(userEmail, -pointsToSpend);
		} catch (EmailIsNotRegisteredException | InvalidEmailAddressException e) {
			throwInvalidEmailFault(e.getMessage());
		} catch (NotEnoughPoints e) {
			throwNotEnoughBalanceFault(e.getMessage());
		}

		return res;
	}

	// QC-supporting methods
	@Override
	public Response<GetBalanceResponse> getBalanceAsync(String userEmail) {
		return null;
	}

	@Override
	public Future<?> getBalanceAsync(String userEmail, AsyncHandler<GetBalanceResponse> asyncHandler) {
		return null;
	}

	@Override
	public TaggedBalance getBalance(String userEmail) throws InvalidEmailFault_Exception {
		return null;
	}

	@Override
	public Response<SetBalanceResponse> setBalanceAsync(String userEmail, TaggedBalance taggedBalance) {
		return null;
	}

	@Override
	public Future<?> setBalanceAsync(String userEmail, TaggedBalance taggedBalance,
			AsyncHandler<SetBalanceResponse> asyncHandler) {
		return null;
	}

	@Override
	public void setBalance(String userEmail, TaggedBalance taggedBalance)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {

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

	/** Helper to throw a new EmailAlreadyExistsFault exception. */
	private void throwEmailAlreadyExists(final String message) throws EmailAlreadyExistsFault_Exception {
		final EmailAlreadyExistsFault faultInfo = new EmailAlreadyExistsFault();
		faultInfo.message = message;
		throw new EmailAlreadyExistsFault_Exception(message, faultInfo);
	}

	/** Helper to throw a new InvalidPointsFault exception. */
	private void throwInvalidPointsFault(final String message) throws InvalidPointsFault_Exception {
		final InvalidPointsFault faultInfo = new InvalidPointsFault();
		faultInfo.message = message;
		throw new InvalidPointsFault_Exception(message, faultInfo);
	}

	/** Helper to throw a new NotEnoughBalanceFault exception. */
	private void throwNotEnoughBalanceFault(final String message) throws NotEnoughBalanceFault_Exception {
		final NotEnoughBalanceFault faultInfo = new NotEnoughBalanceFault();
		faultInfo.message = message;
		throw new NotEnoughBalanceFault_Exception(message, faultInfo);
	}
}