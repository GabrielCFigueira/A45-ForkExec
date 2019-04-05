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
import com.forkexec.hub.domain.exceptions.EmptyCartException;
import com.forkexec.hub.domain.exceptions.NotEnoughPointsException;
import com.forkexec.hub.domain.exceptions.BadInitException;


import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.MenuInit;
import com.forkexec.rst.ws.BadTextFault_Exception;
import com.forkexec.rst.ws.BadMenuIdFault_Exception;
import com.forkexec.rst.ws.InsufficientQuantityFault_Exception;
import com.forkexec.rst.ws.BadQuantityFault_Exception;
import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;



import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.NotEnoughBalanceFault_Exception;
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
		if(userId == null)
			throw new InvalidUserIdException("UserId set to null");
		else if (_users.containsKey(userId))
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

	public List<FoodOrderItem> orderCart(String UDDIUrl, String pointOrgName, String userId) 
			throws EmptyCartException, NoSuchUserException, NotEnoughPointsException, InvalidUserIdException, InvalidFoodQuantityException {

		if (getUser(userId).getCart().isEmpty())
			throw new EmptyCartException("Cart for user " + userId + " is empty");
		
		int pointsToSpend = 0;
		List<FoodOrderItem> order = cartContents(userId);
		for(FoodOrderItem foodOrderItem : order) {
			try {
				pointsToSpend += getFood(UDDIUrl, foodOrderItem.getFoodId().getRestaurantId(), foodOrderItem.getFoodId()).getPrice() * foodOrderItem.getFoodQuantity();
			} catch (InvalidFoodIdException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		try {
			getPointsClient(UDDIUrl, pointOrgName).spendPoints(userId, pointsToSpend);
		} catch (NotEnoughBalanceFault_Exception | InvalidPointsFault_Exception e){
			throw new NotEnoughPointsException(e.getMessage());
		} catch (InvalidEmailFault_Exception e) {
			throw new InvalidUserIdException(e.getMessage());
		}

		for(FoodOrderItem foodOrderItem : order)
			orderMenu(UDDIUrl, foodOrderItem.getFoodId().getRestaurantId(), userId, foodOrderItem.getFoodId(), foodOrderItem.getFoodQuantity());

		getUser(userId).clearCart();
		return order;
	}

	private void orderMenu(String UDDIUrl, String orgName, String userId, FoodId foodId, int quantity) 
			throws InvalidFoodQuantityException {
		RestaurantClient restaurantClient = getRestaurantClient(UDDIUrl, orgName);

		try {
			restaurantClient.orderMenu(foodIdIntoMenuId(foodId), quantity);
		} catch (BadMenuIdFault_Exception e) {
			throw new RuntimeException(e.getMessage());
		} catch(InsufficientQuantityFault_Exception | BadQuantityFault_Exception e) {
			throw new InvalidFoodQuantityException(e.getMessage());
		}
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

	public void ctrlInitPoints(String UDDIUrl, String orgName, int startPoints) throws BadInitException {
		try {
			getPointsClient(UDDIUrl, orgName).ctrlInit(startPoints);
		} catch (com.forkexec.pts.ws.BadInitFault_Exception | RuntimeException e) {
			throw new BadInitException(e.getMessage());
		}
	}

	public void ctrlInitFood(String UDDIUrl, List<FoodInit> initialFoods) throws BadInitException {
		
		if(initialFoods == null)
			throw new BadInitException("List of FoodInit is set to null");

		Map<String, List<MenuInit>> menuList = new TreeMap<String, List<MenuInit>>();

		for (FoodInit foodInit : initialFoods) {
			Food food = foodInit.getFood();
			String restaurantId = food.getId().getRestaurantId();

			if(!menuList.containsKey(restaurantId))
				menuList.put(restaurantId, new ArrayList<MenuInit>());
			menuList.get(restaurantId).add(createMenuInit(createMenu(createMenuId(food.getId().getMenuId()), 
						food.getEntree(), food.getPlate(), food.getDessert(), food.getPrice(), 
						food.getPreparationTime()), foodInit.getFoodQuantity()));
		}

		try {
			RestaurantClient restaurant = null;
			for (String id : menuList.keySet()) {
				restaurant = getRestaurantClient(UDDIUrl, id);
				restaurant.ctrlInit(menuList.get(id));
			}
		} catch (com.forkexec.rst.ws.BadInitFault_Exception | RuntimeException e) {
			throw new BadInitException(e.getMessage());
		}
	}

	public void ctrlClearRestaurant(String UDDIUrl, String orgName) {
		getRestaurantClient(UDDIUrl, orgName).ctrlClear();
	}

	public void ctrlClearPoints(String UDDIUrl, String orgName) {
		getPointsClient(UDDIUrl, orgName).ctrlClear();
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

	private Menu createMenu(MenuId menuId, String entree, String plate, 
                        String dessert, int price, int preparationTime) {
        Menu menu = new Menu();
        menu.setId(menuId);
        menu.setEntree(entree);
        menu.setPlate(plate);
        menu.setDessert(dessert);
        menu.setPrice(price);
        menu.setPreparationTime(preparationTime);

        return menu;
    }

    private MenuInit createMenuInit(Menu menu, int quantity) {
        MenuInit menuInit = new MenuInit();
        menuInit.setMenu(menu);
        menuInit.setQuantity(quantity);
        return menuInit;
    }

    private MenuId createMenuId(String id) {
        MenuId menuId = new MenuId();
        menuId.setId(id);
        return menuId;
    }

}
