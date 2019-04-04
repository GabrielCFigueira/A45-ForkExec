package pt.ulisboa.tecnico.sdis.ws.it;

import pt.ulisboa.tecnico.sdis.ws.cc.CreditCardClient;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class for testing a Park Load properties from test.properties
 */
public class BaseIT {

	protected static CreditCardClient client;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		client = new CreditCardClient();
	}

	@AfterClass
	public static void cleanup() {
	}

}
