package com.forkexec.hub.ws;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


import javax.jws.WebService;

import com.forkexec.hub.domain.Hub;


import com.forkexec.rst.ws.Menu;
import com.forkexec.rst.ws.MenuId;
import com.forkexec.rst.ws.BadMenuIdFault_Exception;
import com.forkexec.rst.ws.cli.RestaurantClient;
import com.forkexec.rst.ws.cli.RestaurantClientException;
import com.forkexec.rst.ws.BadTextFault_Exception;


import com.forkexec.pts.ws.InvalidEmailFault_Exception;
import com.forkexec.pts.ws.EmailAlreadyExistsFault_Exception;
import com.forkexec.pts.ws.cli.PointsClient;
import com.forkexec.pts.ws.cli.PointsClientException;
import com.forkexec.pts.ws.InvalidPointsFault_Exception;
import com.forkexec.pts.ws.NotEnoughBalanceFault_Exception;


import pt.ulisboa.tecnico.sdis.ws.uddi.*;
import pt.ulisboa.tecnico.sdis.ws.*;

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
	private Map<String, RestaurantClient> _restaurants = new TreeMap<String, RestaurantClient>();
	private PointsClient pointsClient = null;


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
			pointsClient.activateUser(userId);
			hub.addUser(userId); //TODO verificar users repetidos?
		} catch (EmailAlreadyExistsFault_Exception | InvalidEmailFault_Exception e) {
			throwInvalidUserIdFault(e.getMessage());
		}
	}

	@Override
	public void loadAccount(String userId, int moneyToAdd, String creditCardNumber)
			throws InvalidCreditCardFault_Exception, InvalidMoneyFault_Exception, InvalidUserIdFault_Exception {

		CreditCardImplService creditCard = new CreditCardImplService();

		try {
			if(!hub.hasUser(userId))
				throwInvalidUserIdFault("No User with such Id: " + userId);
			else if (!creditCard.getCreditCardImplPort().validateNumber(creditCardNumber)) 
				throwInvalidCreditCardFault(creditCardNumber);
			else if (moneyToAdd == 10)
				pointsClient.addPoints(userId, 1000);
			else if (moneyToAdd == 20)
				pointsClient.addPoints(userId, 2100);
			else if (moneyToAdd == 30)
				pointsClient.addPoints(userId, 3300);
			else if (moneyToAdd == 50)
				pointsClient.addPoints(userId, 5500);
			else
				throwInvalidMoneyFault(moneyToAdd);
		} catch (InvalidPointsFault_Exception | InvalidEmailFault_Exception e) {
			throwInvalidUserIdFault(e.getMessage());  //TODO change InvalidPoints to throw invalidMoney
		}
	}
	
	
	@Override
	public List<Food> searchDeal(String description) throws InvalidTextFault_Exception  {

		List<Food> res = new ArrayList<Food>();
		try {
			res = getAllFood(description);
			
			Collections.sort(res, new Comparator<Food>() { 
				@Override
				public int compare(Food food1, Food food2) {
					return food1.getPrice() - food2.getPrice();
				}
			});
		} catch (BadTextFault_Exception e) {
			throwInvalidTextFault(e.getMessage());
		}

		return res;
	}
	
	@Override
	public List<Food> searchHungry(String description) throws InvalidTextFault_Exception {
		
		List<Food> res = new ArrayList<Food>();
		try {
			res = getAllFood(description);

			Collections.sort(res, new Comparator<Food>() { 
				@Override
				public int compare(Food food1, Food food2) {
					return food1.getPreparationTime() - food2.getPreparationTime();
				}
			});
		} catch (BadTextFault_Exception e) {
			throwInvalidTextFault(e.getMessage());
		}

		return res;
	}

	//TODO should the user's cart keep a list of FoodIds or Foods?
	@Override
	public void addFoodToCart(String userId, FoodId foodId, int foodQuantity)
			throws InvalidFoodIdFault_Exception, InvalidFoodQuantityFault_Exception, InvalidUserIdFault_Exception {

		getFood(foodId);
		if(!hub.hasUser(userId))
			throwInvalidUserIdFault("No User with such Id: " + userId);
		else if (foodQuantity < 1 || foodQuantity > 100) //what is the reasonable maximum number?
			throwInvalidFoodQuantityFault(foodQuantity);

		hub.addFood(userId, foodIdIntoDomain(foodId), foodQuantity);

		
	}

	@Override
	public void clearCart(String userId) throws InvalidUserIdFault_Exception {
		if (hub.hasUser(userId))
			hub.clearCart(userId);
		else
			throwInvalidUserIdFault("No User with such Id: " + userId);
	}

	@Override
	public FoodOrder orderCart(String userId)
			throws EmptyCartFault_Exception, InvalidUserIdFault_Exception, NotEnoughPointsFault_Exception {  //TODO NotEnoughPointsFault_Exception
		
		if (!hub.hasUser(userId))
			throwInvalidUserIdFault("No User with such Id: " + userId);
		if (hub.getUser(userId).getCart().isEmpty())
			throwEmptyCartFault(userId);

		FoodOrder foodOrder = new FoodOrder();
		foodOrder.items = cartContents(userId);

		int pointsToSpend = 0;
		for(FoodOrderItem foodOrderItem : foodOrder.items)
			try {
				pointsToSpend += getFood(foodOrderItem.getFoodId()).getPrice();
			} catch (InvalidFoodIdFault_Exception e) {
				//TODO
			}

		try {
			pointsClient.spendPoints(userId, pointsToSpend);
		} catch (NotEnoughBalanceFault_Exception e){
			//TODO
		} catch (InvalidPointsFault_Exception e) {
			//TODO
		} catch (InvalidEmailFault_Exception e) {
			//TODO
		}
		
		foodOrder.setFoodOrderId(new FoodOrderId());  //foodOrderId? TODO

		return foodOrder;
	}

	@Override
	public int accountBalance(String userId) throws InvalidUserIdFault_Exception {

		int balance = 0;
		try {
			balance = pointsClient.pointsBalance(userId);
		} catch (InvalidEmailFault_Exception e) {
			throwInvalidUserIdFault(e.getMessage());
		}
		return balance;
	}

	@Override
	public Food getFood(FoodId foodId) throws InvalidFoodIdFault_Exception {
		Food food = null;
		try {
			food = menuIntoFood(_restaurants.get(foodId.getRestaurantId()).getMenu(foodIdIntoMenuId(foodId)));
		} catch (BadMenuIdFault_Exception e) {
			throwInvalidFoodIdFault(e.getMessage());
		}

		return food;
	}

	@Override
	public List<FoodOrderItem> cartContents(String userId) throws InvalidUserIdFault_Exception {

		if(!hub.hasUser(userId))
			throwInvalidUserIdFault("No User with such Id: " + userId);

		List<FoodOrderItem> res = new ArrayList<FoodOrderItem>();

		for(com.forkexec.hub.domain.FoodOrderItem food : hub.getUser(userId).getCart().getFood())
			res.add(foodOrderItemIntoWs(food));
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
				_restaurants.put(e.getOrgName(), restaurant);
			}

			for(UDDIRecord e: endpointManager.getUddiNaming().listRecords("A45pointsClients%")) { //this should run only 1 time
				PointsClient points = new PointsClient(endpointManager.getUddiNaming().getUDDIUrl(), e.getOrgName());
				builder.append("\n").append(points.ctrlPing("points client"));
				pointsClient = points; //TODO several point severs?
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
		_restaurants = new TreeMap<String, RestaurantClient>();
		pointsClient.ctrlClear();  //TODO sera suposto?
		pointsClient = null;
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInitFood(List<FoodInit> initialFoods) throws InvalidInitFault_Exception {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void ctrlInitUserPoints(int startPoints) throws InvalidInitFault_Exception {
		// TODO Auto-generated method stub
		
	}



	//-------------------------------------------------------------------------


	private List<Food> getAllFood(String description) throws BadTextFault_Exception {
		List<Food> res = new ArrayList<Food>();
		for(RestaurantClient rst : _restaurants.values()) 
			for (Menu menu : rst.searchMenus(description))
				res.add(menuIntoFood(menu));
		return res;
	}


	// View helpers ----------------------------------------------------------

	// /** Helper to convert a domain object to a view. */
	// private ParkInfo buildParkInfo(Park park) {
		// ParkInfo info = new ParkInfo();
		// info.setId(park.getId());
		// info.setCoords(buildCoordinatesView(park.getCoordinates()));
		// info.setCapacity(park.getMaxCapacity());
		// info.setFreeSpaces(park.getFreeDocks());
		// info.setAvailableCars(park.getAvailableCars());
		// return info;
	// }

	private Food menuIntoFood(Menu menu) {
		Food food = new Food();
		FoodId foodId = new FoodId();

		foodId.setRestaurantId(menu.toString());
		foodId.setMenuId(menu.getId().getId());

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



	/** Helpers to throw a new BadInit exception. */


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

	private void throwInvalidFoodQuantityFault(final int foodQuantity) throws InvalidFoodQuantityFault_Exception {
		InvalidFoodQuantityFault invalidFoodQuantityFault = new InvalidFoodQuantityFault();
		invalidFoodQuantityFault.message = "FoodQuantity " + foodQuantity + " is invalid";
		throw new InvalidFoodQuantityFault_Exception("FoodQuantity " + foodQuantity + " is invalid", invalidFoodQuantityFault);
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

	private void throwInvalidMoneyFault(final int moneyToAdd) throws InvalidMoneyFault_Exception {
		InvalidMoneyFault invalidMoneyFault = new InvalidMoneyFault();
		invalidMoneyFault.message = "Invalid money quantity: " + moneyToAdd;
		throw new InvalidMoneyFault_Exception("Invalid money quantity: " + moneyToAdd, invalidMoneyFault);
	}
}
