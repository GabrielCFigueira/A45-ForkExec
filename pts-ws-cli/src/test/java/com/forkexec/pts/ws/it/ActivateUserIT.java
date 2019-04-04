package com.forkexec.pts.ws.it;

import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;

import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests ActivateUser operation
 */
public class ActivateUserIT extends BaseIT {
	
	@Before
	public void setUp(){
		client.ctrlClear();
	}

	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest1() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser(" ");
	}

	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest2() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("@");
	}

	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest3() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser(".");
	}

	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest4() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("0");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest5() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest6() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("_@_");
	}

	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest7() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user @tecnico.ist.utl.pt");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest8() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user@ tecnico.ist.utl.pt");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest9() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user@@tecnico.ist.utl.pt");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest10() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user tecnico");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest11() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user$@tecnico.pt");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest12() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user@tecnico#.com");
	}

	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest13() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user_tecnico@.com");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest14() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user_tecnico@.");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest15() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user..@tecnico.com");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest16() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user@tecnico..com");
	}

	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest17() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user@.tecnico.com");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest18() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("()%&aaa+*@123.tecnico.com");
	}

	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest19() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("@tecnico.com");
	}

	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest20() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user@");
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void invalidEmailTest21() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser(null);
	}
	
	@Test
	public void validEmailTest1() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user3.tecn1co@ulisboa.com");
	}
	
	@Test
	public void validEmailTest2() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user@tecnico");
	}
	
	@Test
	public void validEmailTest3() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("user@tecnico.pt");
	}

	@Test
	public void validEmailTest4() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("123@123");
	}

	@Test
	public void validEmailTest5() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("AAA@AAA");
	}

	@Test
	public void validEmailTest6() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		client.activateUser("Us3R.T.e.S.t@TECNICO.uL1sB0a.Pt");
	}

	@Test (expected = EmailAlreadyExistsFault_Exception.class)
	public void activeSameUserTest() throws EmailAlreadyExistsFault_Exception, InvalidEmailFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.activateUser(mail);
		
		client.activateUser(mail);
	}
}