package com.forkexec.hub.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.FoodId;
import com.forkexec.hub.ws.FoodInit;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import com.forkexec.hub.ws.InvalidFoodIdFault_Exception;
/**
 * Class that tests InitFood operation
 */
public class InitFoodIT extends BaseIT {
	private static final int QNTY = 5;
	private static final FoodId FOODID_R1 = createFoodId("A45_Restaurant1", "Menu1");
	private static final FoodId FOODID_R2 = createFoodId("A45_Restaurant2", "Menu1");
	private static final Food FOOD_R1 = createFood("Bitoque", "Bitoque", "Bitoque", 2, 20, FOODID_R1);
	private static final Food FOOD_R2 = createFood("Pao de alho", "Hamburguer", "Mousse", 2, 20, FOODID_R2);

	// tests
	// assertEquals(expected, actual);

	// public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception 

	@Before
	public void setup() {
		client.ctrlClear();
	}

	@Test
	public void success() throws InvalidInitFault_Exception, InvalidFoodIdFault_Exception {
		client.ctrlInitFood(createFoodInitList(FOOD_R1, QNTY));
		client.ctrlInitFood(createFoodInitList(FOOD_R2, QNTY));

		// see if we can get all the foods
		assertEqualFood(FOOD_R1, client.getFood(FOODID_R1));
		assertEqualFood(FOOD_R2, client.getFood(FOODID_R2));
	}

	@Test(expected = InvalidInitFault_Exception.class)
	public void nullInit() throws InvalidInitFault_Exception {
		client.ctrlInitFood(null);
	}

	@Test
	public void emptyInit() throws InvalidInitFault_Exception {
		client.ctrlInitFood(new ArrayList<FoodInit>());
	}

	@Test(expected = InvalidInitFault_Exception.class)
	public void initWithInvalidRestaurant() throws InvalidInitFault_Exception {
		FOOD_R1.setId(createFoodId("!!INVALID!!", "Menu1"));
		client.ctrlInitFood(createFoodInitList(FOOD_R1, QNTY));
	}
}