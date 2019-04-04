package com.forkexec.hub.ws.it;

import org.junit.Test;
import org.junit.Before;

import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.FoodId;
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
	private static final FoodId FOODID_R1 = createFoodId("A45_Restaurant1", "Menu1");
	private static final FoodId FOODID_R2 = createFoodId("A45_Restaurant2", "Menu1");
	private static final Food FOOD_R1 = createFood("Bitoque", "Bitoque", "Bitoque", 2, 20, FOODID_R1);
	private static final Food FOOD_R2 = createFood("Pao de alho", "Hamburguer", "Mousse", 2, 20, FOODID_R2);
	// assertEquals(expected, actual);

	// public public void addFoodToCart(String userId, FoodId foodId, int foodQuantity)
	// throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception,
	//        InvalidUserIdFault_Exception 

	@Before
	public void setup() throws InvalidInitFault_Exception, InvalidUserIdFault_Exception {
		client.ctrlClear();
		client.ctrlInitFood(createFoodInitList(FOOD_R1, QNTY));
		client.ctrlInitFood(createFoodInitList(FOOD_R2, QNTY));
		client.activateAccount(USER);
	}

	@Test
	public void success() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, FOODID_R1, 1);
		client.addFoodToCart(USER, FOODID_R2, 1);
	}

	@Test
	public void successAddingTwice() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, FOODID_R1, 1);
		client.addFoodToCart(USER, FOODID_R2, 1);
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void nullUser() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(null, FOODID_R1, 1);
	}

	@Test(expected = InvalidUserIdFault_Exception.class)
	public void invalidUser() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart("user2@email", FOODID_R1, 1);
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
		FOODID_R1.setMenuId("!!INVALID!!");
		client.addFoodToCart(USER, FOODID_R1, 1);
	}

	@Test(expected = InvalidFoodQuantityFault_Exception.class)
	public void negativeQuantity() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, FOODID_R1, -1);
	}

	@Test(expected = InvalidFoodQuantityFault_Exception.class)
	public void zeroQuantity() throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {
		client.addFoodToCart(USER, FOODID_R1, 0);
	}
}
