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
	private static FoodId FOODID_R1;
	private static FoodId FOODID_R2;
	private static final Food FOOD_R1 = createFood("Bitoque", "Bitoque", "Bitoque", 2, 20);
	private static final Food FOOD_R2 = createFood("Pao de alho", "Hamburguer", "Mousse", 2, 20);
	// tests
	// assertEquals(expected, actual);

	// public Food getFood(FoodId foodId) throws InvalidFoodIdFault_Exception 

	@Before
	public void setup() throws InvalidInitFault_Exception {
		client.ctrlClear();
		FOODID_R1 = createFoodId("A45_Restaurant1", "Menu1");
		FOODID_R2 = createFoodId("A45_Restaurant2", "Menu1");
		FOOD_R1.setId(FOODID_R1);
		FOOD_R2.setId(FOODID_R2);
		client.ctrlInitFood(createFoodInitList(FOOD_R1, QNTY));
		client.ctrlInitFood(createFoodInitList(FOOD_R2, QNTY));
	}

	@Test
	public void success() throws InvalidFoodIdFault_Exception {
		Food f1 = client.getFood(FOODID_R1);
		Food f2 = client.getFood(FOODID_R2);

		// the food and foodId is equal
		assertEqualFood(FOOD_R1, f1);
		assertEqualFood(FOOD_R2, f2);
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
		FOODID_R1.setRestaurantId(null);
		client.getFood(FOODID_R1);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void invalidRestaurant() throws InvalidFoodIdFault_Exception {
		FOODID_R1.setRestaurantId("!!INVALID!!");
		client.getFood(FOODID_R1);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void nullMenu() throws InvalidFoodIdFault_Exception {
		FOODID_R1.setMenuId(null);
		client.getFood(FOODID_R1);
	}

	@Test(expected = InvalidFoodIdFault_Exception.class)
	public void invalidMenu() throws InvalidFoodIdFault_Exception {
		FOODID_R1.setMenuId("!!INVALID!!");
		client.getFood(FOODID_R1);
	}
}
