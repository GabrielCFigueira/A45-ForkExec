package com.forkexec.pts.ws.it.frontend;

import com.forkexec.pts.ws.cli.exception.EmailAlreadyRegisteredException;
import com.forkexec.pts.ws.cli.exception.InvalidEmailAddressException;

import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests ActivateUser operation
 */
public class ActivateUserIT extends BaseIT {
	
	@Before
	public void setUp(){
		frontend.ctrlClear();
	}

	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest1() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser(" ");
	}

	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest2() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("@");
	}

	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest3() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser(".");
	}

	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest4() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("0");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest5() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest6() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("_@_");
	}

	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest7() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user @tecnico.ist.utl.pt");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest8() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user@ tecnico.ist.utl.pt");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest9() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user@@tecnico.ist.utl.pt");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest10() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user tecnico");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest11() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user$@tecnico.pt");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest12() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user@tecnico#.com");
	}

	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest13() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user_tecnico@.com");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest14() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user_tecnico@.");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest15() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user..@tecnico.com");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest16() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user@tecnico..com");
	}

	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest17() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user@.tecnico.com");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest18() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("()%&aaa+*@123.tecnico.com");
	}

	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest19() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("@tecnico.com");
	}

	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest20() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user@");
	}
	
	@Test (expected = InvalidEmailAddressException.class)
	public void invalidEmailTest21() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser(null);
	}
	
	@Test
	public void validEmailTest1() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user3.tecn1co@ulisboa.com");
	}
	
	@Test
	public void validEmailTest2() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user@tecnico");
	}
	
	@Test
	public void validEmailTest3() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("user@tecnico.pt");
	}

	@Test
	public void validEmailTest4() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("123@123");
	}

	@Test
	public void validEmailTest5() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("AAA@AAA");
	}

	@Test
	public void validEmailTest6() throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		frontend.activateUser("Us3R.T.e.S.t@TECNICO.uL1sB0a.Pt");
	}

	@Test (expected = EmailAlreadyRegisteredException.class)
	public void activeSameUserTest() throws InvalidEmailAddressException, EmailAlreadyRegisteredException{
		String mail = "user@tecnico.ulisboa.pt";
		frontend.activateUser(mail);
		
		frontend.activateUser(mail);
	}
}