package com.forkexec.hub.domain;


import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;


import com.forkexec.hub.domain.exceptions.NoSuchUserException;
import com.forkexec.hub.domain.exceptions.DuplicateUserException;
import com.forkexec.hub.domain.exceptions.MaximumCartQuantityException;
import com.forkexec.hub.domain.exceptions.InvalidFoodQuantityException;
import com.forkexec.hub.domain.exceptions.InvalidCreditCardException;
import com.forkexec.hub.domain.exceptions.InvalidFoodIdException;
import com.forkexec.hub.domain.exceptions.InvalidTextException;
import com.forkexec.hub.domain.exceptions.InvalidUserIdException;
import com.forkexec.hub.domain.exceptions.InvalidMoneyException;


import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.BadTextFault_Exception;
import com.forkexec.rst.ws.BadMenuIdFault_Exception;
import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;



import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;


import pt.ulisboa.tecnico.sdis.ws.cc.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cc.CreditCardClientException;


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

	private Map<String, User> _users = new TreeMap<String, User>();

	public void reset() {
		_users = new TreeMap<String, User>();
	}

	private RestaurantClient getRestaurantClient(String UDDIUrl, String orgName)  {
		try {
			return new RestaurantClient(UDDIUrl, orgName);
		} catch(RestaurantClientException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private PointsClient getPointsClient(String UDDIUrl, String orgName) {
		try {
			return new PointsClient(UDDIUrl, orgName);
		} catch(PointsClientException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public List<Food> getAllFood(String UDDIUrl, String orgName, String description) throws InvalidTextException{
		List<Food> res = new ArrayList<Food>();
		RestaurantClient restaurantClient = getRestaurantClient(UDDIUrl, orgName);
		try {
			for (Menu menu : restaurantClient.searchMenus(description))
				res.add(menuIntoFood(menu, orgName));
		} catch (BadTextFault_Exception e) {
			throw new InvalidTextException(e.getMessage());
		}
		return res;
	}

	public User getUser(String userId) throws NoSuchUserException {
		if (userId == null)
			throw new NoSuchUserException("UserId set to null");
		User user = _users.get(userId);
		if(user == null)
			throw new NoSuchUserException("User with Id: " + userId + " does not exist");
		else
			return user;
	}

	public void addUser(String UDDIUrl, String orgName, String userId) throws DuplicateUserException, InvalidUserIdException {
		PointsClient pointsClient = getPointsClient(UDDIUrl, orgName);
		if (_users.containsKey(userId))
			throw new DuplicateUserException("User with Id: " + userId + " already exists");
		try {
			pointsClient.activateUser(userId);
		} catch (EmailAlreadyExistsFault_Exception | InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException(e.getMessage());
		}
		_users.put(userId, new User(userId));
	}

	public void loadAccount(String UDDIUrl, String orgName, String userId, int moneyToAdd, String creditCardNumber) 
			throws InvalidCreditCardException, InvalidUserIdException, InvalidMoneyException {
		
		PointsClient pointsClient = getPointsClient(UDDIUrl, orgName);
		try {
			CreditCardClient creditCard = new CreditCardClient();

			if (!creditCard.validateNumber(creditCardNumber)) 
				throw new InvalidCreditCardException("Invalid creditcard number: " + creditCardNumber);
			else if (moneyToAdd == 10)
				pointsClient.addPoints(userId, 1000);
			else if (moneyToAdd == 20)
				pointsClient.addPoints(userId, 2100);
			else if (moneyToAdd == 30)
				pointsClient.addPoints(userId, 3300);
			else if (moneyToAdd == 50)
				pointsClient.addPoints(userId, 5500);
			else
				throw new InvalidMoneyException("Invalid money quantity: " + moneyToAdd);
		} catch (InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException(e.getMessage());
		} catch (InvalidPointsFault_Exception e) {
			throw new InvalidMoneyException(e.getMessage());
		} catch (CreditCardClientException e) {
			throw new InvalidCreditCardException(e.getMessage());
		}
	}

	public void addFood(String userId, FoodId foodId, int quantity) 
			throws NoSuchUserException, MaximumCartQuantityException, InvalidFoodQuantityException {
		getUser(userId).addFood(foodId, quantity);
	}

	public void clearCart(String userId) throws NoSuchUserException {
		getUser(userId).clearCart();
	}

	public int accountBalance(String UDDIUrl, String orgName, String userId) throws InvalidUserIdException {
		PointsClient pointsClient = getPointsClient(UDDIUrl, orgName);
		try {
			return pointsClient.pointsBalance(userId);
		} catch (InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException(e.getMessage());
		}
	}

	public Food getFood(String UDDIUrl, String orgName, FoodId foodId) throws InvalidFoodIdException {
		RestaurantClient restaurantClient = getRestaurantClient(UDDIUrl, orgName);
		try {
			return menuIntoFood(restaurantClient.getMenu(foodIdIntoMenuId(foodId)), orgName);
		} catch (BadMenuIdFault_Exception e) {
			throw new InvalidFoodIdException(e.getMessage());
		}
	}

	public List<FoodOrderItem> cartContents(String userId) throws NoSuchUserException {
		List<FoodOrderItem> res = new ArrayList<FoodOrderItem>();
		for(FoodOrderItem foodOrderItem : getUser(userId).getCart().getFood().values())
			res.add(foodOrderItem);
		return res;
	}



	//-------------------------------UTILS-----------------------------------------------------


	private Food menuIntoFood(Menu menu, String restaurantId) {
		Food food = new Food();
		FoodId foodId = new FoodId(restaurantId, menu.getId().getId());

		food.setId(foodId);
		food.setEntree(menu.getEntree());
		food.setPlate(menu.getPlate());
		food.setDessert(menu.getDessert());
		food.setPrice(menu.getPrice());
		food.setPreparationTime(menu.getPreparationTime());

		return food;
	}

	private MenuId foodIdIntoMenuId(FoodId foodId) {
		MenuId menuId = new MenuId();
		menuId.setId(foodId.getMenuId());

		return menuId;
	}

}
