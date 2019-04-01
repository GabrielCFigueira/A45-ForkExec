package pt.ulisboa.tecnico.sdis.ws.cc;

/** 
 * Client application. 
 * 
 * Looks for Hub using UDDI and arguments provided
 */
public class CreditCardClientApp {

	public static void main(String[] args) throws Exception {
		// Check arguments.
		if (args.length == 0) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + CreditCardClientApp.class.getName() + " wsURL OR uddiURL wsName");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		if (args.length == 1) {
			wsURL = args[0];
		} else if (args.length >= 2) {
			uddiURL = args[0];
			wsName = args[1];
		}

		// Create client.
		CreditCardClient client = null;

		if (wsURL != null) {
			System.out.printf("Creating client for server at %s%n", wsURL);
			client = new CreditCardClient(wsURL);
		} else if (uddiURL != null) {
			System.out.printf("Creating client using UDDI at %s for server with name %s%n", uddiURL, wsName);
			client = new CreditCardClient(uddiURL, wsName);
		}

		// The following remote invocation is just a basic example.
		// The actual tests are made using JUnit.

		System.out.println("Invoke ping()...");
		String result = client.ping("client");

		client.ping("test");
		System.out.print("Result: ");
		System.out.println(result);
	}

}
