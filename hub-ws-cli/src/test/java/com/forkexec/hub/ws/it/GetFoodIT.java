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
import com.forkexec.hub.ws.InvalidInitFault_Exception;

/**
 * Class that tests getFood operation
 */
public class GetFoodIT extends BaseIT {
	private static final int QNTY = 5;
	private static FoodId FOODID;
	private static final Food FOOD = createFood("Bitoque", "Bitoque", "Bitoque", 2, 20, FOODID);
	// tests
	// assertEquals(expected, actual);

	// public Food getFood(FoodId foodId) throws InvalidFoodIdFault_Exception 

	@Before
	public void setup() throws InvalidInitFault_Exception {
		client.ctrlClear();
		FOODID = createFoodId("A45_Restaurant1", "Menu1");
		client.ctrlInitFood(createFoodInitList(FOOD, QNTY));
	}

	@Test
	public void success() throws InvalidFoodIdFault_Exception {
		Food f = client.getFood(FOODID);

		// the food and foodId is equal
		assertEqualFood(FOOD, f);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void nullFoodId() throws InvalidFoodIdFault_Exception {
		client.getFood(null);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void emptyFoodId() throws InvalidFoodIdFault_Exception {
		client.getFood(new FoodId());
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void nullRestaurant() throws InvalidFoodIdFault_Exception {
		FOODID.setRestaurantId(null);
		client.getFood(FOODID);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void invalidRestaurant() throws InvalidFoodIdFault_Exception {
		FOODID.setRestaurantId("!!INVALID!!");
		client.getFood(FOODID);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void nullMenu() throws InvalidFoodIdFault_Exception {
		FOODID.setMenuId(null);
		client.getFood(FOODID);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void invalidMenu() throws InvalidFoodIdFault_Exception {
		FOODID.setMenuId("!!INVALID!!");
		client.getFood(FOODID);
	}
}
