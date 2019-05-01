package com.forkexec.pts.ws.it;

import com.forkexec.pts.ws.BadInitFault_Exception;
import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.TaggedBalance;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Class that tests Actions operation
 */
public class ActionsClientIT extends BaseClientIT {
	
	@Before
	public void setUp(){
		client.ctrlClear();
	}
	
	@Test
	public void addPointsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		TaggedBalance balance = new TaggedBalance();
		balance.setPoints(50);
		balance.setTag(1);
		
		client.setBalance(EMAIL, balance);
		
		assertEqualTaggedBalance(balance, client.getBalance(EMAIL));
	}
	
	@Test(expected = InvalidPointsFault_Exception.class)
	public void removePointsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		TaggedBalance balance = new TaggedBalance();
		balance.setPoints(-200);
		balance.setTag(1);
		
		client.setBalance(EMAIL, balance);
	}
	
	@Test(expected = InvalidEmailFault_Exception.class)
	public void nullEmailPointsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		TaggedBalance balance = new TaggedBalance();
		balance.setPoints(-200);
		balance.setTag(1);
		
		client.setBalance(null, balance);
	}
	
	@Test(expected = InvalidEmailFault_Exception.class)
	public void invalidEmailPointsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		TaggedBalance balance = new TaggedBalance();
		balance.setPoints(-200);
		balance.setTag(1);
		
		client.setBalance("abcd", balance);
	}
	
	@Test
	public void newUserWithInitTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, BadInitFault_Exception {
		client.ctrlInit(25);
		
		assertEquals(25, client.getBalance(EMAIL).getPoints());
	}
	
	/*@Test (expected = InvalidEmailFault_Exception.class)
	public void addPointsUserNotExistsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		client.addPoints(EMAIL, 150);
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void spendPointsUserNotExistsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, NotEnoughBalanceFault_Exception {
		client.spendPoints(EMAIL, 150);
	}
	
	@Test (expected = InvalidEmailFault_Exception.class)
	public void pointsBalanceUserNotExistsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception {
		client.pointsBalance(EMAIL);
	}
	
	@Test (expected = EmailAlreadyExistsFault_Exception.class)
	public void addPointsUserExistsTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, NotEnoughBalanceFault_Exception {
		client.addPoints(EMAIL, 150);
		
		client.activateUser(EMAIL);
	}
	
	@Test (expected = EmailAlreadyExistsFault_Exception.class)
	public void spendPointsSameUserTest() throws InvalidEmailFault_Exception, InvalidPointsFault_Exception, EmailAlreadyExistsFault_Exception, NotEnoughBalanceFault_Exception {
		client.spendPoints(EMAIL, 50);
		
		client.activateUser(EMAIL);
	}*/
	
}