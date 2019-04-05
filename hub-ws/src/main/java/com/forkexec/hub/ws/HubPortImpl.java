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
import com.forkexec.hub.domain.exceptions.EmptyCartException;
import com.forkexec.hub.domain.exceptions.NotEnoughPointsException;
import com.forkexec.hub.domain.exceptions.BadInitException;


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
		} catch (UDDINamingException e) {
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
		
		List<String> restaurantInfo = new ArrayList<String>();
		String UDDIUrl = endpointManager.getUddiNaming().getUDDIUrl();

		List<com.forkexec.hub.domain.FoodOrderItem> order = null;
		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Points%")) {
				order = hub.orderCart(UDDIUrl, e.getOrgName(), userId);
				break;
			}
		} catch (EmptyCartException e) {
			throwEmptyCartFault(e.getMessage());
		} catch (NoSuchUserException | InvalidUserIdException e) {
			throwInvalidUserIdFault(e.getMessage());
		} catch (NotEnoughPointsException e) {
			throwNotEnoughPointsFault(e.getMessage());
		} catch (InvalidFoodQuantityException e) {
			throwInvalidFoodQuantityFault(e.getMessage());
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}

		List<FoodOrderItem> foodItems = new ArrayList<FoodOrderItem>();
		for (com.forkexec.hub.domain.FoodOrderItem food : order)
			foodItems.add(foodOrderItemIntoWs(food));

		FoodOrder foodOrder = new FoodOrder();
		foodOrder.items = foodItems;


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
		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Restaurant%"))
				hub.ctrlClearRestaurant(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName());
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Points%")) //this should run only once
				hub.ctrlClearPoints(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName());
		} catch(UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInitFood(List<FoodInit> initialFoods) throws InvalidInitFault_Exception {

		List<com.forkexec.hub.domain.FoodInit> domainInitialFoods = new ArrayList<com.forkexec.hub.domain.FoodInit>();

		for(FoodInit foodInit : initialFoods)
				domainInitialFoods.add(foodInitIntoDomain(foodInit));
		try {
			hub.ctrlInitFood(endpointManager.getUddiNaming().getUDDIUrl(), domainInitialFoods);
		} catch (BadInitException e) {
			throwInvalidInitFault(e.getMessage());
		}
	}
	
	@Override
	public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception {
		try {
			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45_Points%"))  //this should run only once
				hub.ctrlInitPoints(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName(), startPoints);
		} catch (BadInitException e) {
			throwInvalidInitFault(e.getMessage());
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}



	//-------------------------------------------------------------------------

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

	private com.forkexec.hub.domain.FoodInit foodInitIntoDomain(FoodInit foodInit) {
		return new com.forkexec.hub.domain.FoodInit(foodIntoDomain(foodInit.getFood()), foodInit.getQuantity());
	}

	private com.forkexec.hub.domain.Food foodIntoDomain(Food food) {

		com.forkexec.hub.domain.Food domainFood = new com.forkexec.hub.domain.Food();
        domainFood.setId(foodIdIntoDomain(food.getId()));
        domainFood.setEntree(food.getEntree());
        domainFood.setPlate(food.getPlate());
        domainFood.setDessert(food.getDessert());
        domainFood.setPrice(food.getPrice());
        domainFood.setPreparationTime(food.getPreparationTime());

        return domainFood;
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
