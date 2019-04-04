package com.forkexec.hub.ws.it;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;

/**
 * Class that tests Ping operation
 */
public class AccountBalanceIT extends BaseIT {
	private static final int INITIAL_POINTS = 200;
	private static final String USER = "user@email";
	// tests
	// assertEquals(expected, actual);

	// public int accountBalance(String userId) throws InvalidUserIdFault_Exception 

	@Before
	public void setup() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
		client.ctrlClear();
		client.ctrlInitUserPoints(INITIAL_POINTS);
		client.activateAccount(USER);
	}


	@Test
	public void verifyInitialPoints() throws InvalidUserIdFault_Exception {
		assertEquals(INITIAL_POINTS, client.accountBalance(USER));
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void nullId() throws InvalidUserIdFault_Exception {
		client.accountBalance(null);
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void unknownID() throws InvalidUserIdFault_Exception {
		client.accountBalance("user2@email");
	}

	@Test
	public void verifyAfterCharging10() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
		client.loadAccount(USER, 10, "4024007102923926");
		assertEquals(INITIAL_POINTS + 1000, client.accountBalance(USER));
	}

	@Test
	public void verifyAfterCharging20() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
		client.loadAccount(USER, 20, "4024007102923926");
		assertEquals(INITIAL_POINTS + 2100, client.accountBalance(USER));
	}

	@Test
	public void verifyAfterCharging30() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
		client.loadAccount(USER, 30, "4024007102923926");
		assertEquals(INITIAL_POINTS + 3300, client.accountBalance(USER));
	}

	@Test
	public void verifyAfterCharging50() throws InvalidUserIdFault_Exception, InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception {
		client.loadAccount(USER, 50, "4024007102923926");
		assertEquals(INITIAL_POINTS + 5500, client.accountBalance(USER));
	}
}
