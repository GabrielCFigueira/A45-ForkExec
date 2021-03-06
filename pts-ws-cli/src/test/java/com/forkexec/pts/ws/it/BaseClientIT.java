package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import com.forkexec.pts.ws.TaggedBalance;
import com.forkexec.pts.ws.cli.PointsClient;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class for testing a Park Load properties from test.properties
 */
public class BaseClientIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;
	
	protected static String EMAIL = "user@tecnico.ulisboa.pt";

	protected static PointsClient client;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseClientIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file %s", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps.getProperty("uddi.enabled");
		final String verboseEnabled = testProps.getProperty("verbose.enabled");

		final String uddiURL = testProps.getProperty("uddi.url");
		final String wsName = testProps.getProperty("ws.name");
		final String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client = new PointsClient(uddiURL, wsName);
		} else {
			client = new PointsClient(wsURL);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));
	}

	@AfterClass
	public static void cleanup() {
	}
	
	public void assertEqualTaggedBalance(TaggedBalance b1, TaggedBalance b2) {
		assertEquals(b1.getPoints(), b2.getPoints());
		assertEquals(b1.getTag(), b2.getTag());
	}
}
