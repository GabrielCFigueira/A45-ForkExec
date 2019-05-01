package com.forkexec.pts.ws.it.frontend;

import java.io.IOException;
import java.util.Properties;

import com.forkexec.pts.ws.cli.PointsFrontEnd;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Base class for testing a Park Load properties from test.properties
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static PointsFrontEnd frontend;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file %s", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		frontend = new PointsFrontEnd(3);
	}

	@AfterClass
	public static void cleanup() {
	}
}
