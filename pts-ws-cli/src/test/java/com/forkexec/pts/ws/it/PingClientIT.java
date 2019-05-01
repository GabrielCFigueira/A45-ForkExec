package com.forkexec.pts.ws.it;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Class that tests Ping operation
 */
public class PingClientIT extends BaseClientIT {

	// tests
	// assertEquals(expected, actual);

	// public String ping(String x)

	@Test
	public void pingEmptyTest() {
		assertNotNull(client.ctrlPing("test"));
	}

}
