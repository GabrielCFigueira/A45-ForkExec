package com.forkexec.hub.ws;

import javax.jws.WebService;


import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.RuntimeException;




import com.forkexec.hub.domain.Hub;
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
import com.forkexec.rst.ws.MenuInit;
import com.forkexec.rst.ws.BadMenuIdFault_Exception;
import com.forkexec.rst.ws.BadTextFault_Exception;
import com.forkexec.rst.ws.BadInitFault;
import com.forkexec.rst.ws.BadInitFault_Exception;
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


import pt.ulisboa.tecnico.sdis.ws.uddi.*;
import pt.ulisboa.tecnico.sdis.ws.cc.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cc.CreditCardClientException;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.hub.ws.HubPortType",
            wsdlLocation = "HubService.wsdl",
            name ="HubWebService",
            portName = "HubPort",
            targetNamespace="http://ws.hub.forkexec.com/",
            serviceName = "HubService"
)
public class HubPortImpl implements HubPortType {


	private Hub hub = Hub.getInstance();
	private int foodOrderId = 0;

	//TODO synchronized


	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private HubEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public HubPortImpl(HubEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
	
	// Main operations -------------------------------------------------------
	
	@Override
	public void activateAccount(String userId) throws InvalidUserIdFault_Exception {

		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Points%"))
				hub.addUser(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName(), userId);
		} catch (InvalidUserIdException | DuplicateUserException e) {
			throwInvalidUserIdFault(e.getMessage());
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void loadAccount(String userId, int moneyToAdd, String creditCardNumber)
			throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {

		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Points%"))
				hub.loadAccount(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName(), userId, moneyToAdd, creditCardNumber);
		} catch (InvalidUserIdException e) {
			throwInvalidUserIdFault(e.getMessage());
		} catch (InvalidMoneyException e) {
			throwInvalidMoneyFault(e.getMessage());
		} catch (InvalidCreditCardException e) {
			throwInvalidCreditCardFault(e.getMessage());
		} catch(UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@Override
	public List<Food> searchDeal(String description) throws InvalidTextFault_Exception  {

		if (description == null)
			throwInvalidTextFault("Given description is null");

		List<Food> res = new ArrayList<Food>();
		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Restaurant%")) {
				List<com.forkexec.hub.domain.Food> domainFood = hub.getAllFood(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName(), description);
				for(com.forkexec.hub.domain.Food food : domainFood)
					res.add(foodIntoWs(food));
			
			}
			Collections.sort(res, new Comparator<Food>() { 
				@Override
				public int compare(Food food1, Food food2) {
					return food1.getPrice() - food2.getPrice();
				}
			});
		} catch (InvalidTextException e) {
			throwInvalidTextFault(e.getMessage());
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}

		return res;
	}
	
	@Override
	public List<Food> searchHungry(String description) throws InvalidTextFault_Exception {
		
		if (description == null)
			throwInvalidTextFault("Given description is null");

		List<Food> res = new ArrayList<Food>();
		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Restaurant%")) {
				List<com.forkexec.hub.domain.Food> domainFood = hub.getAllFood(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName(), description);
				for(com.forkexec.hub.domain.Food food : domainFood)
					res.add(foodIntoWs(food));
			}					
			Collections.sort(res, new Comparator<Food>() { 
				@Override
				public int compare(Food food1, Food food2) {
					return food1.getPreparationTime() - food2.getPreparationTime();
				}
			});
		} catch (InvalidTextException e) {
			throwInvalidTextFault(e.getMessage());
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}

		return res;
	}

	@Override
	public void addFoodToCart(String userId, FoodId foodId, int foodQuantity)
			throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {

		getFood(foodId); //testing if the food exists; will throw InvalidFoodIdFaultException if not TODO check this

		try {
			hub.addFood(userId, foodIdIntoDomain(foodId), foodQuantity);
		} catch (NoSuchUserException e) {
			throwInvalidUserIdFault(e.getMessage());
		} catch (MaximumCartQuantityException | InvalidFoodQuantityException e) {
			throwInvalidFoodQuantityFault(e.getMessage());
		}
	}

	@Override
	public void clearCart(String userId) throws InvalidUserIdFault_Exception {
		try {
			hub.clearCart(userId);
		} catch (NoSuchUserException e) {
			throwInvalidUserIdFault(e.getMessage());
		}
	}

	@Override
	public FoodOrder orderCart(String userId)
			throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception, InvalidFoodQuantityFault_Exception {
		
		try {
			if (hub.getUser(userId).getCart().isEmpty())
				throwEmptyCartFault(userId);
		} catch (NoSuchUserException e) {
			throwInvalidUserIdFault(e.getMessage());
		}

		FoodOrder foodOrder = new FoodOrder();
		foodOrder.items = cartContents(userId);

		int pointsToSpend = 0;
		for(FoodOrderItem foodOrderItem : foodOrder.items) {
			try {
				pointsToSpend += getFood(foodOrderItem.getFoodId()).getPrice() * foodOrderItem.getFoodQuantity();
			} catch (InvalidFoodIdFault_Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		try {
			getPointsClient().spendPoints(userId, pointsToSpend);
		} catch (NotEnoughBalanceFault_Exception | InvalidPointsFault_Exception e){
			throwNotEnoughPointsFault(e.getMessage());
		} catch (InvalidEmailFault_Exception e) {
			throwInvalidUserIdFault(e.getMessage());
		}

		Map<String, RestaurantClient> restaurants = getRestaurants();

		for (FoodOrderItem foodOrderItem : foodOrder.items) {
			FoodId foodId = foodOrderItem.getFoodId();
			RestaurantClient restaurant = restaurants.get(foodId.getRestaurantId());
			if(restaurant == null)
				throw new RuntimeException("Restaurant " + foodId.getRestaurantId() + " was not found");
			
			try {
				restaurant.orderMenu(foodIdIntoMenuId(foodId), foodOrderItem.getFoodQuantity());
			} catch (BadMenuIdFault_Exception e) {
				throw new RuntimeException(e.getMessage());
		 	} catch(InsufficientQuantityFault_Exception | BadQuantityFault_Exception e) {
				throwInvalidFoodQuantityFault(e.getMessage());
			}
		}

		FoodOrderId id = new FoodOrderId();
		id.setId(Integer.toString(foodOrderId++));
		foodOrder.setFoodOrderId(id);

		return foodOrder;
	}

	@Override
	public int accountBalance(String userId) throws InvalidUserIdFault_Exception {

		int balance = -1;
		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Points%"))
				balance = hub.accountBalance(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName(), userId);
		} catch (InvalidUserIdException e) {
			throwInvalidUserIdFault(e.getMessage());
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return balance;
	}

	@Override
	public Food getFood(FoodId foodId) throws InvalidFoodIdFault_Exception {

		Food food = null;
		
		if(foodId == null || foodId.getRestaurantId() == null || foodId.getMenuId() == null)
			throwInvalidFoodIdFault("Invalid foodId");

		String restaurantId = foodId.getRestaurantId();
		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Restaurant%"))
				if(e.getOrgName().equals(restaurantId))
					food = foodIntoWs(hub.getFood(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName(), foodIdIntoDomain(foodId)));
		} catch (InvalidFoodIdException e) {
			throwInvalidFoodIdFault(e.getMessage());
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}

		if (food == null)
			throwInvalidFoodIdFault("FoodId does not belong to any known Restaurant");

		return food;
	}

	@Override
	public List<FoodOrderItem> cartContents(String userId) throws InvalidUserIdFault_Exception {

		List<FoodOrderItem> res = new ArrayList<FoodOrderItem>();
		try {
			for(com.forkexec.hub.domain.FoodOrderItem foodOrderItem : hub.cartContents(userId))
				res.add(foodOrderItemIntoWs(foodOrderItem));
		} catch (NoSuchUserException e) {
			throwInvalidUserIdFault(e.getMessage());
		}
		return res;

	}

	// Control operations ----------------------------------------------------

	/** Diagnostic operation to check if service is running. */
	@Override
	public String ctrlPing(String inputMessage) {
		try {
			// If no input is received, return a default name.
			if (inputMessage == null || inputMessage.trim().length() == 0)
				inputMessage = "friend";

			// If the service does not have a name, return a default.
			String wsName = endpointManager.getWsName();
			if (wsName == null || wsName.trim().length() == 0)
				wsName = "Hub";

			// Build a string with a message to return.
			StringBuilder builder = new StringBuilder();
			builder.append("Hello ").append(inputMessage);
			builder.append(" from ").append(wsName);

			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Restaurant%")) {
				RestaurantClient restaurant = new RestaurantClient(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName());
				builder.append("\n").append(restaurant.ctrlPing("restaurant client"));
			}

			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Points%")) {
				PointsClient points = new PointsClient(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName());
				builder.append("\n").append(points.ctrlPing("points client"));
			}

			return builder.toString();
		
		} catch(UDDINamingException | RestaurantClientException | PointsClientException e) {
			return e.getMessage().toString();
		}
	}

	/** Return all variables to default values. */
	@Override
	public void ctrlClear() {
		hub.reset();
		for(RestaurantClient rst : getRestaurants().values())
			rst.ctrlClear();
		getPointsClient().ctrlClear();
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInitFood(List<FoodInit> initialFoods) throws InvalidInitFault_Exception {

		if(initialFoods == null)
			throwInvalidInitFault("List of FoodInit is set to null");

		Map<String, List<MenuInit>> menuList = new TreeMap<String, List<MenuInit>>();
		Map<String, RestaurantClient> restaurants = getRestaurants();

		for (FoodInit foodInit : initialFoods) {
			Food food = foodInit.getFood();
			String restaurantId = food.getId().getRestaurantId();

			if (!restaurants.containsKey(restaurantId))
				throwInvalidInitFault("Unknown Restaurant " + restaurantId);

			if(!menuList.containsKey(restaurantId))
				menuList.put(restaurantId, new ArrayList<MenuInit>());
			menuList.get(restaurantId).add(createMenuInit(createMenu(createMenuId(food.getId().getMenuId()), 
						food.getEntree(), food.getPlate(), food.getDessert(), food.getPrice(), 
						food.getPreparationTime()), foodInit.getQuantity()));
		}

		try {
			for (String id : menuList.keySet())
				restaurants.get(id).ctrlInit(menuList.get(id));
		} catch (BadInitFault_Exception e) {
			throwInvalidInitFault(e.getMessage());
		}
	}
	
	@Override
	public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception {
		
		try {
			getPointsClient().ctrlInit(startPoints);
		} catch (com.forkexec.pts.ws.BadInitFault_Exception e) {
			throwInvalidInitFault(e.getMessage());
		}
	}



	//-------------------------------------------------------------------------


	private Map<String, RestaurantClient> getRestaurants() {
		Map<String, RestaurantClient> restaurants = new ConcurrentHashMap<String, RestaurantClient>();
		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Restaurant%")) {
				RestaurantClient restaurant = new RestaurantClient(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName());
				restaurants.put(e.getOrgName(), restaurant);
			}
		} catch(UDDINamingException | RestaurantClientException e) {
			throw new RuntimeException(e.getMessage());
		}
		return restaurants;
	}

	private PointsClient getPointsClient() {
		PointsClient pointsClient = null;
		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Points%")) { //this should run only once
				pointsClient = new PointsClient(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName());
			}
		} catch(UDDINamingException | PointsClientException e) {
			throw new RuntimeException(e.getMessage());
		}
		if(pointsClient == null)
			throw new RuntimeException("No point server was found");
		return pointsClient;
	}

	private MenuId foodIdIntoMenuId(FoodId foodId) {
		MenuId menuId = new MenuId();
		menuId.setId(foodId.getMenuId());

		return menuId;
	}

	private Food foodIntoWs(com.forkexec.hub.domain.Food domainFood) {
		Food food = new Food();
        food.setId(foodIdIntoWs(domainFood.getId()));
        food.setEntree(domainFood.getEntree());
        food.setPlate(domainFood.getPlate());
        food.setDessert(domainFood.getDessert());
        food.setPrice(domainFood.getPrice());
        food.setPreparationTime(domainFood.getPreparationTime());

        return food;
	}

	private FoodId foodIdIntoWs(com.forkexec.hub.domain.FoodId domainFoodId) {
		FoodId foodId = new FoodId();
		foodId.setRestaurantId(domainFoodId.getRestaurantId());
		foodId.setMenuId(domainFoodId.getMenuId());
		return foodId;
	}

	private FoodOrderItem foodOrderItemIntoWs(com.forkexec.hub.domain.FoodOrderItem food) {
		FoodOrderItem res = new FoodOrderItem();
		FoodId foodId = new FoodId();
		foodId.setRestaurantId(food.getFoodId().getRestaurantId());
		foodId.setMenuId(food.getFoodId().getMenuId());
		res.setFoodId(foodId);
		res.setFoodQuantity(food.getFoodQuantity());
		return res;
	}

	private com.forkexec.hub.domain.FoodId foodIdIntoDomain(FoodId foodId) {
		return new com.forkexec.hub.domain.FoodId(foodId.getRestaurantId(), foodId.getMenuId());
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




	/** Helpers to throw exceptions. */


	private void throwInvalidUserIdFault(final String message) throws InvalidUserIdFault_Exception {
		InvalidUserIdFault invalidUserIdFault = new InvalidUserIdFault();
		invalidUserIdFault.message = message;
		throw new InvalidUserIdFault_Exception(message, invalidUserIdFault);
	}

	private void throwInvalidFoodIdFault(final String message) throws InvalidFoodIdFault_Exception {
		InvalidFoodIdFault invalidFoodIdFault = new InvalidFoodIdFault();
		invalidFoodIdFault.message = message;
		throw new InvalidFoodIdFault_Exception(message, invalidFoodIdFault);
	}

	private void throwEmptyCartFault(final String userId) throws EmptyCartFault_Exception {
		EmptyCartFault emptyCartFault = new EmptyCartFault();
		emptyCartFault.message = "The Cart for user " + userId + " is empty";
		throw new EmptyCartFault_Exception("The Cart for user " + userId + " is empty", emptyCartFault);
	}

	private void throwInvalidFoodQuantityFault(final String message) throws InvalidFoodQuantityFault_Exception {
		InvalidFoodQuantityFault invalidFoodQuantityFault = new InvalidFoodQuantityFault();
		invalidFoodQuantityFault.message = message;
		throw new InvalidFoodQuantityFault_Exception(message, invalidFoodQuantityFault);
	}

	private void throwInvalidTextFault(final String message) throws InvalidTextFault_Exception {
		InvalidTextFault invalidTextFault = new InvalidTextFault();
		invalidTextFault.message = message;
		throw new InvalidTextFault_Exception(message, invalidTextFault);
	}

	private void throwInvalidCreditCardFault(final String creditCardNumber) throws InvalidCreditCardFault_Exception {
		InvalidCreditCardFault invalidCreditCardFault = new InvalidCreditCardFault();
		invalidCreditCardFault.message = "Invalid credit card number: " + creditCardNumber;
		throw new InvalidCreditCardFault_Exception("Invalid credit card number: " + creditCardNumber, invalidCreditCardFault);
	}

	private void throwInvalidMoneyFault(final String message) throws InvalidMoneyFault_Exception {
		InvalidMoneyFault invalidMoneyFault = new InvalidMoneyFault();
		invalidMoneyFault.message = message;
		throw new InvalidMoneyFault_Exception(message, invalidMoneyFault);
	}

	private void throwInvalidInitFault(final String message) throws InvalidInitFault_Exception {
		InvalidInitFault invalidInitFault = new InvalidInitFault();
		invalidInitFault.message = message;
		throw new InvalidInitFault_Exception(message, invalidInitFault);
	}

	private void throwNotEnoughPointsFault(final String message) throws NotEnoughPointsFault_Exception {
		NotEnoughPointsFault notEnoughPointsFault = new NotEnoughPointsFault();
		notEnoughPointsFault.message = message;
		throw new NotEnoughPointsFault_Exception(message, notEnoughPointsFault);
	}
}
