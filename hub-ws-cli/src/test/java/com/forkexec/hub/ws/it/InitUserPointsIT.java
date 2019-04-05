package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
/**
 * Class that tests Ping operation
 */
public class InitUserPointsIT extends BaseIT {
	private static final String USER = "user@email";

	// tests
	// assertEquals(expected, actual);

	// public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception 

	@Before
	public void setup() {
		client.ctrlClear();
	}

	@Test
	public void defaultValueInit() throws InvalidUserIdFault_Exception {
		client.activateAccount(USER);
		assertEquals(100, client.accountBalance(USER));
	}

	@Test
	public void setSpecificValueTest() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
		client.ctrlInitUserPoints(234);
		client.activateAccount(USER);
		assertEquals(234, client.accountBalance(USER));
	}

	@Test
	public void initWith0() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
		client.ctrlInitUserPoints(0);
		client.activateAccount(USER);
		assertEquals(0, client.accountBalance(USER));
	}

	@Test(expected = InvalidInitFault_Exception.class)
	public void initWithNegativePoints() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
		client.ctrlInitUserPoints(-1);
	}

}