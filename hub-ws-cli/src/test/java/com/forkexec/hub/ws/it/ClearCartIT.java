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
	private static final FoodId FOODID_R1 = createFoodId("A45_Restaurant1", "Menu1");
	private static final FoodId FOODID_R2 = createFoodId("A45_Restaurant2", "Menu1");
	private static final Food FOOD_R1 = createFood("Bitoque", "Bitoque", "Bitoque", 2, 20, FOODID_R1);
	private static final Food FOOD_R2 = createFood("Pao de alho", "Hamburguer", "Mousse", 2, 20, FOODID_R2);

	// tests
	// assertEquals(expected, actual);

	// public List<FoodOrderItem> cartContents(String userId) throws InvalidUserIdFault_Exception

	@Before
	public void setup() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidFoodIdFault_Exception {
		client.ctrlClear();
		client.ctrlInitFood(createFoodInitList(FOOD_R1, QNTY));
		client.ctrlInitFood(createFoodInitList(FOOD_R2, QNTY));
		client.activateAccount(USER);
		client.addFoodToCart(USER, FOODID_R1, ASKED_QNTY);
		client.addFoodToCart(USER, FOODID_R2, ASKED_QNTY);
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
		assertEquals(ASKED_QNTY * 2, client.cartContents(USER).size());
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
