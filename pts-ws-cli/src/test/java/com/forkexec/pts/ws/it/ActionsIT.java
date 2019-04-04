package com.forkexec.pts.ws.it;

import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.NotEnoughBalanceFault_Exception;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Actions operation
 */
public class ActionsIT extends BaseIT {
	
	@Before
	public void clear(){
		client.ctrlClear();
	}
	
	@Test
	public void addPointsTest() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, InvalidPointsFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.activateUser(mail);
		
		client.addPoints(mail, 50);
		
		assertEquals(150, client.pointsBalance(mail));
	}
	
	@Test(expected = InvalidPointsFault_Exception.class)
	public void removePointsTest() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, InvalidPointsFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.activateUser(mail);
		
		client.addPoints(mail, -200);
	}
	
	@Test
	public void addAndRemovePointsTest() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.activateUser(mail);
		
		client.addPoints(mail, 50);
		
		client.spendPoints(mail, 150);
		
		assertEquals(0, client.pointsBalance(mail));
	}
	
	@Test (expected = InvalidPointsFault_Exception.class)
	public void spendPointsTest() throws InvalidEmailFault_Exception, EmailAlreadyExistsFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.activateUser(mail);
		
		client.addPoints(mail, 150);
		
		client.spendPoints(mail, 250);
		
		client.addPoints(mail, 100);
		
		client.spendPoints(mail, -101);
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void addPointsUserNotExistsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.addPoints(mail, 150);
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void spendPointsUserNotExistsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.spendPoints(mail, 150);
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void pointsBalanceUserNotExistsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.pointsBalance(mail);
	}
	
	@Test (expected = EmailAlreadyExistsFault_Exception.class)
	public void addPointsUserExistsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, NotEnoughBalanceFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.activateUser(mail);
		client.addPoints(mail, 150);
		
		client.activateUser(mail);
	}
	
	@Test (expected = EmailAlreadyExistsFault_Exception.class)
	public void spendPointsSameUserTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, NotEnoughBalanceFault_Exception {
		String mail = "user@tecnico.ulisboa.pt";
		client.activateUser(mail);
		client.spendPoints(mail, 50);
		
		client.activateUser(mail);
	}
	
	@Test
	public void addAndSpendPointsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, NotEnoughBalanceFault_Exception, BadInitFault_Exception {
		String mail1 = "user1@tecnico.ulisboa.pt";
		client.activateUser(mail1);
		client.addPoints(mail1, 50);
		
		String mail2 = "user2@tecnico.ulisboa.pt";
		client.activateUser(mail2);
		client.spendPoints(mail2, 50);
		
		assertEquals(150, client.pointsBalance(mail1));
		assertEquals(50, client.pointsBalance(mail2));
		
		client.ctrlClear();
		client.ctrlInit(10);
		
		client.activateUser(mail1);
		client.activateUser(mail2);
		
		assertEquals(10, client.pointsBalance(mail1));
		assertEquals(10, client.pointsBalance(mail2));
	}
	
	@Test (expected = EmailAlreadyExistsFault_Exception.class)
	public void addAndSpendPointsError1Test() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, NotEnoughBalanceFault_Exception, BadInitFault_Exception {
		String mail1 = "user1@tecnico.ulisboa.pt";
		client.activateUser(mail1);
		client.addPoints(mail1, 50);
		
		String mail2 = "user2@tecnico.ulisboa.pt";
		client.activateUser(mail2);
		client.spendPoints(mail2, 50);
		
		assertEquals(150, client.pointsBalance(mail1));
		assertEquals(50, client.pointsBalance(mail2));
		
		client.ctrlInit(10);
		
		client.activateUser(mail1);
		client.activateUser(mail2);
		
		assertEquals(10, client.pointsBalance(mail1));
		assertEquals(10, client.pointsBalance(mail2));
	}
	
	@Test (expected = NotEnoughBalanceFault_Exception.class)
	public void addAndSpendPointsError2Test() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, NotEnoughBalanceFault_Exception, BadInitFault_Exception {
		String mail1 = "user1@tecnico.ulisboa.pt";
		client.activateUser(mail1);
		assertEquals(100, client.pointsBalance(mail1));
		
		client.ctrlInit(10);
		String mail2 = "user2@tecnico.ulisboa.pt";
		client.activateUser(mail2);
		client.spendPoints(mail2, 50);
	}
	
	@Test
	public void newUserWithInitTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, NotEnoughBalanceFault_Exception, BadInitFault_Exception {
		String mail1 = "user1@tecnico.ulisboa.pt";
		client.activateUser(mail1);
		
		client.ctrlInit(25);
		
		String mail2 = "user2@tecnico.ulisboa.pt";
		client.activateUser(mail2);
		
		client.spendPoints(mail1, 20);
		client.spendPoints(mail2, 20);
		
		assertEquals(80, client.pointsBalance(mail1));
		assertEquals(5, client.pointsBalance(mail2));
	}
	
}