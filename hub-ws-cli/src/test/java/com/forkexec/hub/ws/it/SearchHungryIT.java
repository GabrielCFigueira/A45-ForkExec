package com.forkexec.hub.ws.it;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.FoodId;
import com.forkexec.hub.ws.FoodInit;
import com.forkexec.hub.ws.InvalidInitFault_Exception;
import com.forkexec.hub.ws.InvalidTextFault_Exception;

/**
 * Class that tests searchDeal operation
 */
public class SearchHungryIT extends BaseIT {
	private static final int QNTY = 1000;
	private static final FoodId FOODID1 = createFoodId("A45_Restaurant1", "Menu1");
	private static final FoodId FOODID2 = createFoodId("A45_Restaurant1", "Menu2");
	private static final Food FOOD1 = createFood("Bitoque Soberbo", "Bitoque Bom", "Bitoque Fantástico", 20, 20, FOODID1);
	private static final Food FOOD2 = createFood("Bitoque Horrível", "Bitoque Mau", "Bitoque Terrível", 15, 15, FOODID2);

	// tests
	// assertEquals(expected, actual);

	// public List<Food> searchDeal(String description) throws InvalidTextFault_Exception 
	@Before
	public void setup() throws InvalidInitFault_Exception {
		client.ctrlClear();
		List<FoodInit> a = createFoodInitList(FOOD1, QNTY);
		a.add(createFoodInit(FOOD2, QNTY));
		client.ctrlInitFood(a);
	}


	@Test
	public void searchWithZeroResult() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("XYZ");
		assertEquals(0, res.size());
	}

	@Test
	public void searchWithOneResult() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("Bom");
		assertEquals(1, res.size());
		assertEqualFood(FOOD1, res.get(0));
	}

	@Test
	public void searchWithTwoResults() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("Bitoque");
		assertEquals(2, res.size());
		assertEqualFood(FOOD2, res.get(0));
		assertEqualFood(FOOD1, res.get(1));
	}

	@Test
	public void parcialSearchWithTwoResults() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("B");
		assertEquals(2, res.size());
		assertEqualFood(FOOD2, res.get(0));
		assertEqualFood(FOOD1, res.get(1));
	}

	@Test(expected = InvalidTextFault_Exception.class)
	public void nullText() throws InvalidTextFault_Exception {
		client.searchDeal(null);
	}

	@Test(expected = InvalidTextFault_Exception.class)
	public void textWithSpaces() throws InvalidTextFault_Exception {
		client.searchDeal("B B");
	}

	@Test(expected = InvalidTextFault_Exception.class)
	public void emptyText() throws InvalidTextFault_Exception {
		client.searchDeal("");
	}
}
