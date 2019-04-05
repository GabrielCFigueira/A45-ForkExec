package com.forkexec.hub.ws.it;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.FoodId;
import com.forkexec.hub.ws.FoodInit;
import com.forkexec.hub.ws.FoodOrder;
import com.forkexec.hub.ws.FoodOrderItem;
import com.forkexec.hub.ws.InvalidFoodIdFault_Exception;
import com.forkexec.hub.ws.InvalidFoodQuantityFault_Exception;
import com.forkexec.hub.ws.InvalidUserIdFault_Exception;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import com.forkexec.hub.ws.NotEnoughPointsFault_Exception;
import com.forkexec.hub.ws.EmptyCartFault_Exception;

/**
 * Class that tests orderCart operation
 */
public class OrderCartIT extends BaseIT {
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
	public void success() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception {
		List<FoodOrderItem> res = client.orderCart(USER).getItems();

		assertEqualFoodId(FOODID_R1, res.get(0).getFoodId());
		assertEquals(ASKED_QNTY, res.get(1).getFoodQuantity());
		
		assertEqualFoodId(FOODID_R2, res.get(1).getFoodId());
		assertEquals(ASKED_QNTY, res.get(1).getFoodQuantity());
	}

	@Test
	public void verifyCartIsEmpty() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception {
		client.orderCart(USER);
		assertEquals(0, client.cartContents(USER).size());
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void nullUser() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception {
		client.orderCart(null);
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void unregisteredUser() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception {
		client.orderCart("user2@email");
	}

	@Test(expected = EmptyCartFault_Exception.class)
	public void registeredUserWithEmptyCart() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception {
		client.activateAccount("user2@email");
		client.orderCart("user2@email");
	}

	@Test(expected = NotEnoughPointsFault_Exception.class)
	public void notEnoughPoints() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception, InvalidFoodIdFault_Exception, InvalidInitFault_Exception {
		client.ctrlInitUserPoints(1);
		client.activateAccount("user2@email");
		client.addFoodToCart("user2@email", FOODID_R1, 1);
		client.orderCart("user2@email");
	}

	@Test(expected = InvalidFoodQuantityFault_Exception.class)
	public void tooMuchFood() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception, InvalidFoodIdFault_Exception {
		client.addFoodToCart(USER, FOODID_R1, QNTY);
		client.orderCart(USER);
	}

	@Test(expected = InvalidFoodQuantityFault_Exception.class)
	public void foodAlreadyTaken() throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception, InvalidFoodIdFault_Exception {
		client.activateAccount("user2@email");
		client.addFoodToCart("user2@email", FOODID_R1, QNTY);
		try {
			client.orderCart("user2@email"); // all the food is checked out here
		} catch(Exception e) {
			fail("Exception thrown in wrong orderCart()");
		}
		client.orderCart(USER);
	}
}
