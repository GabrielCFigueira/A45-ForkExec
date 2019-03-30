package com.forkexec.hub.domain;


import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;
import com.forkexec.rst.ws.BadTextFault_Exception;
import com.forkexec.rst.ws.Menu;
import com.forkexec.hub.ws.Food;
import com.forkexec.hub.ws.FoodId;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Hub
 *
 * A restaurants hub server.
 *
 */
public class Hub {


	// Singleton -------------------------------------------------------------

	/** Private constructor prevents instantiation from other classes. */
	private Hub() {
		// Initialization of default values
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Hub INSTANCE = new Hub();
	}

	public static synchronized Hub getInstance() {
		return SingletonHolder.INSTANCE;
	}


	private List<RestaurantClient> _restaurants = new ArrayList<RestaurantClient>();
	private Map<FoodId, Food> _foods = null;

	public void addRestaurant(RestaurantClient restaurant) {
		_restaurants.add(restaurant);
	}

	public Map<FoodId, Food> getFoods(String description) throws BadTextFault_Exception {
		if (_foods == null) {
			_foods = new TreeMap<FoodId, Food>();
			List<Menu> menus;
			Food food;
			FoodId foodId;
			for (RestaurantClient rst : _restaurants) {
				menus = rst.searchMenus(description);
				for (Menu menu : menus) {
					food = new Food();
					foodId = new FoodId();

					foodId.setRestaurantId(menu.toString());
					foodId.setMenuId(menu.getId().getId());

					food.setId(foodId);
					food.setEntree(menu.getEntree());
					food.setPlate(menu.getPlate());
					food.setDessert(menu.getDessert());
					food.setPrice(menu.getPrice());
					food.setPreparationTime(menu.getPreparationTime());

					_foods.put(foodId, food);
				}
			}
		}

	return _foods;
	}

	
}
