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

		// Create client.
		CreditCardClient client = new CreditCardClient();
		System.out.printf("Creating client for server at %s%n", client.getWsURL());	

		// The following remote invocation is just a basic example.
		// The actual tests are made using JUnit.

		System.out.println("Invoke ping()...");
		String result = client.ping("client");

		client.ping("test");
		System.out.print("Result: ");
		System.out.println(result);
	}

}
