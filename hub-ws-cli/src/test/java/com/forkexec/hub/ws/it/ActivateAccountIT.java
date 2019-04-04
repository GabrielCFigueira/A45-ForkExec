package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.Before;

import com.forkexec.hub.ws.InvalidUserIdFault_Exception;

/**
 * Class that tests Ping operation
 */
public class ActivateAccountIT extends BaseIT {

	// public void activateAccount(String userID)

	@Before
	public void resetHub() {
		client.ctrlClear();
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void nullEmail() throws InvalidUserIdFault_Exception {
		client.activateAccount(null);
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest0() throws InvalidUserIdFault_Exception {
		client.activateAccount("@");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest1() throws InvalidUserIdFault_Exception {
		client.activateAccount("user@");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest2() throws InvalidUserIdFault_Exception {
		client.activateAccount("@email");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest3() throws InvalidUserIdFault_Exception {
		client.activateAccount("user @email");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest4() throws InvalidUserIdFault_Exception {
		client.activateAccount("user@ email");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest5() throws InvalidUserIdFault_Exception {
		client.activateAccount("user@@email");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest6() throws InvalidUserIdFault_Exception {
		client.activateAccount("us@er@email");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest7() throws InvalidUserIdFault_Exception {
		client.activateAccount("user@em‽ail");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest8() throws InvalidUserIdFault_Exception {
		client.activateAccount("us‽er@email");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest9() throws InvalidUserIdFault_Exception {
		client.activateAccount("user@email..com");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidEmailTest10() throws InvalidUserIdFault_Exception {
		client.activateAccount("us..er@email");
	}

	@Test
	public void validEmail() throws InvalidUserIdFault_Exception {
		client.activateAccount("user@email");
	}

	@Test
	public void validEmails() throws InvalidUserIdFault_Exception {
		client.activateAccount("user@email");
		client.activateAccount("user2@email");
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void duplicateEmail() throws InvalidUserIdFault_Exception {
		client.activateAccount("user@email");
		client.activateAccount("user@email");
	}
}