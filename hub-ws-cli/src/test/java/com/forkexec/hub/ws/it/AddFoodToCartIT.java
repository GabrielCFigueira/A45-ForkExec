package com.forkexec.hub.ws.it;

import org.junit.Test;
import org.junit.Before;

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
 * Class that tests addFoodToCart operation
 */
public class AddFoodToCartIT extends BaseIT {
	private static final int QNTY = 5;
	private static final String USER = "user@email";
	private static final FoodId FOODID = createFoodId("A45_Restaurant1", "Menu1");
	private static final Food FOOD = createFood("Bitoque", "Bitoque", "Bitoque", 2, 20, FOODID);
	// assertEquals(expected, actual);

	// public public void addFoodToCart(String userId, FoodId foodId, int foodQuantity)
	// throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception,
	//        InvalidUserIdFault_Exception 

	@Before
	public void setup() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
		client.ctrlClear();
		client.ctrlInitFood(createFoodInitList(FOOD, QNTY));
		client.activateAccount(USER);
	}

	@Test
	public void success() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, FOODID, 1);
	}

	@Test
	public void successAddingTwice() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, FOODID, 1);
		client.addFoodToCart(USER, FOODID, 1);
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void nullUser() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(null, FOODID, 1);
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidUser() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart("user2@email", FOODID, 1);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void nullFoodId() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, null, 1);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void emptyFoodId() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, new FoodId(), 1);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void invalidFoodId() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		FOODID.setMenuId("!!INVALID!!");
		client.addFoodToCart(USER, FOODID, 1);
	}

	@Test(expected = InvalidFoodQuantityFault_Exception.class)
	public void negativeQuantity() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, FOODID, -1);
	}

	@Test(expected = InvalidFoodQuantityFault_Exception.class)
	public void zeroQuantity() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, FOODID, 0);
	}
}
