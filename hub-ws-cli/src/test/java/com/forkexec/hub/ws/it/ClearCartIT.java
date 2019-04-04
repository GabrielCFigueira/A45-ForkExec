package com.forkexec.hub.ws.it;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.FoodId;
import com.forkexec.hub.ws.FoodInit;
import com.forkexec.hub.ws.InvalidFoodIdFault_Exception;
import com.forkexec.hub.ws.InvalidFoodQuantityFault_Exception;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;

/**
 * Class that tests eartContents operation
 */
public class ClearCartIT extends BaseIT {
	private static final int QNTY = 5;
	private static final int ASKED_QNTY = 1;
	private static final String USER = "user@email";
	private static final FoodId FOODID = createFoodId("A45_Restaurant1", "Menu1");
	private static final Food FOOD = createFood("Bitoque", "Bitoque", "Bitoque", 2, 20, FOODID);

	// tests
	// assertEquals(expected, actual);

	// public List<FoodOrderItem> cartContents(String userId) throws InvalidUserIdFault_Exception

	@Before
	public void setup() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidFoodIdFault_Exception {
		client.ctrlClear();
		client.ctrlInitFood(createFoodInitList(FOOD, QNTY));
		client.activateAccount(USER);
		client.addFoodToCart(USER, FOODID, ASKED_QNTY);
	}

	@Test
	public void success() throws InvalidUserIdFault_Exception {
		client.clearCart(USER);
		assertEquals(0, client.cartContents(USER).size());
	}

	@Test
	public void registeredUserEmptyCart() throws InvalidUserIdFault_Exception {
		client.activateAccount("user2@email");
		client.clearCart("user2@email");
		assertEquals(0, client.cartContents("user2@email").size());
		assertEquals(ASKED_QNTY, client.cartContents(USER).size());
	}


	@Test(expected = InvalidUserIdFault_Exception.class)
	public void nullUser() throws InvalidUserIdFault_Exception {
		client.clearCart(null);
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidUser() throws InvalidUserIdFault_Exception {
		client.clearCart("user3@email");
	}
	
}
