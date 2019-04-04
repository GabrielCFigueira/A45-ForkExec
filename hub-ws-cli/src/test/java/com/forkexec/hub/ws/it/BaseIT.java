package com.forkexec.hub.ws.it;

import java.io.IOException;
import java.util.Properties;

import com.forkexec.hub.ws.cli.HubClient;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.FoodId;
import com.forkexec.hub.ws.FoodInit;

/**
 * Base class for testing a Park Load properties from test.properties
 */
public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static HubClient client;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		final String uddiEnabled = testProps.getProperty("uddi.enabled");
		final String verboseEnabled = testProps.getProperty("verbose.enabled");

		final String uddiURL = testProps.getProperty("uddi.url");
		final String wsName = testProps.getProperty("ws.name");
		final String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			client = new HubClient(uddiURL, wsName);
		} else {
			client = new HubClient(wsURL);
		}
		client.setVerbose("true".equalsIgnoreCase(verboseEnabled));
	}

	@AfterClass
	public static void cleanup() {
	}

	protected static FoodId createFoodId(String restaurantId, String menuId) {
		FoodId res = new FoodId();
		res.setRestaurantId(restaurantId);
		res.setMenuId(menuId);
		return res;
	}

	protected static Food createFood(String entree, String plate, String dessert, int price, int prepTime) {
		Food res = new Food();
		res.setEntree(entree);
		res.setPlate(plate);
		res.setDessert(dessert);
		res.setPrice(price);
		res.setPreparationTime(prepTime);
		return res;
	}

	protected static Food createFood(String entree, String plate, String dessert, int price, int prepTime, FoodId id) {
		Food res = createFood(entree, plate, dessert, price, prepTime);
		res.setId(id);
		return res;
	}

	protected static FoodInit createFoodInit(Food f, int quantity) {
		FoodInit res = new FoodInit();
		res.setFood(f);
		res.setQuantity(quantity);
		return res;
	}

	protected static List<FoodInit> createFoodInitList(Food f, int quantity) {
		List<FoodInit> res = new ArrayList<>();
		res.add(createFoodInit(f, quantity));
		return res;
	}

	protected static void assertEqualFood(Food expected, Food actual) {

		assertEquals(expected.getEntree(),          actual.getEntree());
		assertEquals(expected.getPlate(),           actual.getPlate());
		assertEquals(expected.getDessert(),         actual.getDessert());
		assertEquals(expected.getPrice(),           actual.getPrice());
		assertEquals(expected.getPreparationTime(), actual.getPreparationTime());

		assertEqualFoodId(expected.getId(), actual.getId());
	}

	protected static void assertEqualFoodId(FoodId expected, FoodId actual) {

		assertEquals(expected.getRestaurantId(), actual.getRestaurantId());
		assertEquals(expected.getMenuId(),       actual.getMenuId());

	}
}
