package com.forkexec.pts.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.Map;
import java.util.concurrent.Future;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;

import com.forkexec.pts.ws.*;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

/**
 * Client port wrapper.
 *
 * Adds easier end point address configuration to the Port generated by
 * wsimport.
 */
public class PointsClient implements PointsPortType {

	/** WS service */
	PointsService service = null;

	/** WS port (port type is the interface, port is the implementation) */
	PointsPortType port = null;

	/** UDDI server URL */
	private String uddiURL = null;

	/** WS name */
	private String wsName = null;

	/** WS end point address */
	private String wsURL = null; // default value is defined inside WSDL

	public String getWsURL() {
		return wsURL;
	}

	/** output option **/
	private boolean verbose = false;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided web service URL */
	public PointsClient(String wsURL) throws PointsClientException {
		this.wsURL = wsURL;
		createStub();
	}

	/** constructor with provided UDDI location and name */
	public PointsClient(String uddiURL, String wsName) throws PointsClientException {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		uddiLookup();
		createStub();
	}

	/** UDDI lookup */
	private void uddiLookup() throws PointsClientException {
		try {
			if (verbose)
				System.out.printf("Contacting UDDI at %s%n", uddiURL);
			UDDINaming uddiNaming = new UDDINaming(uddiURL);

			if (verbose)
				System.out.printf("Looking for '%s'%n", wsName);
			wsURL = uddiNaming.lookup(wsName);

		} catch (Exception e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", uddiURL);
			throw new PointsClientException(msg, e);
		}

		if (wsURL == null) {
			String msg = String.format("Service with name %s not found on UDDI at %s", wsName, uddiURL);
			throw new PointsClientException(msg);
		}
	}

	/** Stub creation and configuration */
	private void createStub() {
		if (verbose)
			System.out.println("Creating stub ...");
		service = new PointsService();
		port = service.getPointsPort();

		if (wsURL != null) {
			if (verbose)
				System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, wsURL);
		}
	}

	// remote invocation methods ----------------------------------------------

	@Override
	public void activateUser(String userEmail) throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		port.activateUser(userEmail);
	}

	@Override
	public int pointsBalance(String userEmail) throws InvalidEmailFault_Exception {
		return port.pointsBalance(userEmail);
	}

	@Override
	public int addPoints(String userEmail, int pointsToAdd)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		return port.addPoints(userEmail, pointsToAdd);
	}

	@Override
	public int spendPoints(String userEmail, int pointsToSpend)
			throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
		return port.spendPoints(userEmail, pointsToSpend);
	}

	// new QC methods ---------------------------------------------------------

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

	// control operations -----------------------------------------------------

	@Override
	public String ctrlPing(String inputMessage) {
		return port.ctrlPing(inputMessage);
	}

	@Override
	public void ctrlClear() {
		port.ctrlClear();
	}

	@Override
	public void ctrlInit(int startPoints) throws BadInitFault_Exception {
		port.ctrlInit(startPoints);
	}

}
