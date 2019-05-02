package com.forkexec.hub.ws.it;

import org.junit.Test;
import org.junit.Before;

import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidMoneyFault_Exception;
import com.forkexec.hub.ws.InvalidCreditCardFault_Exception;
/**
 * Class that tests loadAccount operation
 */
public class LoadAccountIT extends BaseIT {
	private static final String USER = "user@email";
	private static final String CC = "4024007102923926";

	//public void loadAccount(String userId, int moneyToAdd, String creditCardNumber)
	// throws InvalidCreditCardFault_Exception,
	//         InvalidMoneyFault_Exception,
	//         InvalidUserIdFault_Exception  

	@Before
	public void setupHub() {
		try {
			client.ctrlClear();
			client.activateAccount(USER);
		} catch (InvalidUserIdFault_Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validCharging() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
		client.loadAccount(USER, 20, CC);
	}

	@Test//Before P2 - (expected = InvalidUserIdFault_Exception.class)
	public void invalidUser() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
		client.loadAccount("user2@email", 20, CC);
	}

	@Test(expected = InvalidMoneyFault_Exception.class)
	public void negativeChargingAmount() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
		client.loadAccount(USER, -1, CC);
	}

	@Test(expected = InvalidMoneyFault_Exception.class)
	public void invalidChargingAmount() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
		client.loadAccount(USER, 21, CC);
	}

	@Test(expected = InvalidCreditCardFault_Exception.class)
	public void invalidCC() throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {
		client.loadAccount(USER, 20, "1");
	}

}
