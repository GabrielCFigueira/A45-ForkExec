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
public class SearchDealIT extends BaseIT {
	private static final int QNTY = 1000;
	private static final FoodId FOODID1_R1 = createFoodId("A45_Restaurant1", "Menu1");
	private static final FoodId FOODID2_R1 = createFoodId("A45_Restaurant1", "Menu2");
	private static final Food FOOD1_R1 = createFood("Bitoque Soberbo", "Bitoque Bom", "Bitoque Fantástico", 20, 20, FOODID1_R1);
	private static final Food FOOD2_R1 = createFood("Bitoque Saboroso", "Bitoque Mau", "Bitoque Terrível", 15, 15, FOODID2_R1);
	
	private static final FoodId FOODID1_R2 = createFoodId("A45_Restaurant2", "Menu1");
	private static final FoodId FOODID2_R2 = createFoodId("A45_Restaurant2", "Menu2");
	private static final Food FOOD1_R2 = createFood("Pao de alho Soberbo", "Hamburguer Bom", "Mousse Fantástico", 10, 10, FOODID1_R2);
	private static final Food FOOD2_R2 = createFood("Pao de alho Horrível", "Hamburguer Mau", "Mousse Terrível", 5, 5, FOODID2_R2);

	private static final FoodId FOODID3_R1 = createFoodId("A45_Restaurant1", "Menu3");
	private static final FoodId FOODID3_R2 = createFoodId("A45_Restaurant2", "Menu3");
	private static final Food FOOD3_R1 = createFood("Francesinha", "Francesinha", "Francesinha", 1000, 1000, FOODID3_R1);
	private static final Food FOOD3_R2 = createFood("Francesinha", "Francesinha", "Francesinha", 1001, 1001, FOODID3_R2);


	// tests
	// assertEquals(expected, actual);

	// public List<Food> searchDeal(String description) throws InvalidTextFault_Exception 
	@Before
	public void setup() throws InvalidInitFault_Exception {
		client.ctrlClear();
		List<FoodInit> a = createFoodInitList(FOOD1_R1, QNTY);
		a.add(createFoodInit(FOOD2_R1, QNTY));
		a.add(createFoodInit(FOOD1_R2, QNTY));
		a.add(createFoodInit(FOOD2_R2, QNTY));
		a.add(createFoodInit(FOOD3_R1, QNTY));
		a.add(createFoodInit(FOOD3_R2, QNTY));
		client.ctrlInitFood(a);
	}


	@Test
	public void searchWithZeroResult() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("XYZ");
		assertEquals(0, res.size());
	}
	
	@Test
	public void searchWithOneResult() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("Saboroso");
		assertEquals(1, res.size());
		assertEqualFood(FOOD2_R1, res.get(0));
	}

	@Test
	public void searchWithTwoResults1() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("Bom");
		assertEquals(2, res.size());
		assertEqualFood(FOOD1_R2, res.get(0));
		assertEqualFood(FOOD1_R1, res.get(1));
	}

	@Test
	public void searchWithTwoResults2() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("Bitoque");
		assertEquals(2, res.size());
		assertEqualFood(FOOD2_R1, res.get(0));
		assertEqualFood(FOOD1_R1, res.get(1));
	}
	
	@Test
	public void searchWithTwoResults3() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("Pao");
		assertEquals(2, res.size());
		assertEqualFood(FOOD2_R2, res.get(0));
		assertEqualFood(FOOD1_R2, res.get(1));
	}

	@Test
	public void parcialSearchWithThreeResults() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("B");
		assertEquals(3, res.size());
		assertEqualFood(FOOD1_R2, res.get(0));
		assertEqualFood(FOOD2_R1, res.get(1));
		assertEqualFood(FOOD1_R1, res.get(2));
	}
	
	@Test
	public void parcialSearchWithFourResults() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("o");
		assertEquals(4, res.size());
		assertEqualFood(FOOD2_R2, res.get(0));
		assertEqualFood(FOOD1_R2, res.get(1));
		assertEqualFood(FOOD2_R1, res.get(2));
		assertEqualFood(FOOD1_R1, res.get(3));
	}

	@Test
	public void searchWithTwoResultsDifferentRestaurants() throws InvalidTextFault_Exception {
		List<Food> res = client.searchDeal("Francesinha");
		assertEqualFood(FOOD3_R1, res.get(0));
		assertEqualFood(FOOD3_R2, res.get(1));
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
	
	@Test(expected = InvalidTextFault_Exception.class)
	public void searchWithTextWithSpaces() throws InvalidTextFault_Exception {
		client.searchDeal("Bitoque Bom");
	}
}
