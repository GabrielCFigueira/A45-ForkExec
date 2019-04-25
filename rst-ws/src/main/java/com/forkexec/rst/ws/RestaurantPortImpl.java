package com.forkexec.rst.ws;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import com.forkexec.rst.domain.Restaurant;
import com.forkexec.rst.domain.RestaurantMenu;
import com.forkexec.rst.domain.RestaurantMenuOrder;
import com.forkexec.rst.domain.exception.InsufficientQuantityException;
import com.forkexec.rst.domain.exception.BadQuantityException;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
@WebService(endpointInterface = "com.forkexec.rst.ws.RestaurantPortType",
            wsdlLocation = "RestaurantService.wsdl",
            name ="RestaurantWebService",
            portName = "RestaurantPort",
            targetNamespace="http://ws.rst.forkexec.com/",
            serviceName = "RestaurantService"
)
public class RestaurantPortImpl implements RestaurantPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private RestaurantEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public RestaurantPortImpl(RestaurantEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
	
	// Main operations -------------------------------------------------------
	
	@Override
	public Menu getMenu(MenuId menuId) throws BadMenuIdFault_Exception {
		if(menuId == null || menuId.getId() == null)
			throwBadMenuId("MenuId cannot be null!");
		
		Restaurant restaurantInstance = Restaurant.getInstance();
		RestaurantMenu menu = restaurantInstance.getMenu(menuId.getId());
		
		if(restaurantInstance.getMenu(menuId.getId()) == null)
			throwBadMenuId("Invalid MenuId: " + menuId.getId() + ". Does not exist!");
			
		return createMenu(menu);
	}
	
	@Override
	public List<Menu> searchMenus(String descriptionText) throws BadTextFault_Exception {
		if(descriptionText == null)
			throwBadText("Description text cannot be null!");
		
		Restaurant restaurantInstance = Restaurant.getInstance();
		
		if(!restaurantInstance.availableString(descriptionText, false))
			throwBadText("Invalid description: " + descriptionText + ".");
		
		List<Menu> newMenuList = new ArrayList<>();
		List<RestaurantMenu> menusList = restaurantInstance.getMenusByDescription(descriptionText);
		
		for(RestaurantMenu menu: menusList)
			newMenuList.add(createMenu(menu));
			
		return newMenuList;
	}

	@Override
	public MenuOrder orderMenu(MenuId arg0, int arg1) throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
		if(arg0 == null || arg0.getId() == null)
			throwBadMenuId("MenuId is null!");
		
		Restaurant restaurantInstance = Restaurant.getInstance();
		if(restaurantInstance.getMenu(arg0.getId()) == null)
			throwBadMenuId("Invalid MenuId: " + arg0.getId());
			
		try {
			RestaurantMenuOrder menu =  restaurantInstance.orderMenu(arg0.getId(), arg1);
			return createMenuOrder(menu);
		}catch(InsufficientQuantityException e) {
			throwInsufficientQuantity("Cannot create an order because quantity menu is insufficient. Input: " + arg1);
		}catch(BadQuantityException e) {
			throwBadQuantity("Quantity cannot be negative. Input: " + arg1);
		}
		
		return null;
	}
	
	// Control operations ----------------------------------------------------

	/** Diagnostic operation to check if service is running. */
	@Override
	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// If the park does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Restaurant";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}

	/** Return all variables to default values. */
	@Override
	public void ctrlClear() {
		Restaurant.getInstance().reset();
	}

	/** Set variables with specific values. */
	@Override
	public void ctrlInit(List<MenuInit> initialMenus) throws BadInitFault_Exception {
		if(initialMenus == null) 
			throwBadInit("Initial menus are null!");
		
		Restaurant restaurantInstance = Restaurant.getInstance();
		
		for(MenuInit menu: initialMenus) {
			
			if(restaurantInstance.getMenu(menu.getMenu().getId().getId()) != null) {
				throwBadInit("Duplicate MenuId '" + menu.getMenu().getId().getId() + "'");
				break;
			}
			
			restaurantInstance.newMenu(menu.getMenu().getId().getId(), menu.getMenu().getEntree(), menu.getMenu().getPlate(), menu.getMenu().getDessert(), menu.getMenu().getPrice(), menu.getMenu().getPreparationTime(), menu.getQuantity());
			
			if(restaurantInstance.getMenu(menu.getMenu().getId().getId()) == null) {
				String error = "Invalid initialMenu:\n";
				error += "\tMenuId: " + menu.getMenu().getId().getId();
				error += "\tEntree: " + menu.getMenu().getEntree();
				error += "\tPlate: " + menu.getMenu().getPlate();
				error += "\tDessert: " + menu.getMenu().getDessert();
				error += "\tPrice: " + menu.getMenu().getPrice();
				error += "\tPreparationTime: " + menu.getMenu().getPreparationTime();
				error += "\tQuantity: " + menu.getQuantity();
				throwBadInit(error);
				break;
			}
		}
	}

	// View helpers ----------------------------------------------------------
	
	private Menu createMenu(RestaurantMenu menu) {
		Menu newMenu = new Menu();
		MenuId newMenuId = new MenuId();
		
		newMenuId.setId(menu.getId());
		
		newMenu.setId(newMenuId);
		newMenu.setEntree(menu.getEntree());
		newMenu.setPlate(menu.getPlate());
		newMenu.setDessert(menu.getDessert());
		newMenu.setPrice(menu.getPrice());
		newMenu.setPreparationTime(menu.getPreparationTime());
		
		return newMenu;
	}
	
	private MenuOrder createMenuOrder(RestaurantMenuOrder menu) {
		MenuOrder newMenu = new MenuOrder();

		MenuOrderId newMenuOrderId = new MenuOrderId();
		newMenuOrderId.setId(menu.getId());
		
		MenuId newMenuId = new MenuId();
		newMenuId.setId(menu.getMenuId());
		
		newMenu.setId(newMenuOrderId);
		newMenu.setMenuId(newMenuId);
		newMenu.setMenuQuantity(menu.getMenuQuantity());
		
		return newMenu;
	}
	
	// Exception helpers -----------------------------------------------------

	/** Helper to throw a new BadInit exception. */
	private void throwBadInit(final String message) throws BadInitFault_Exception {
		BadInitFault faultInfo = new BadInitFault();
		faultInfo.message = message;
		throw new BadInitFault_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new BadMenuId exception. */
	private void throwBadMenuId(final String message) throws BadMenuIdFault_Exception {
		BadMenuIdFault faultInfo = new BadMenuIdFault();
		faultInfo.message = message;
		throw new BadMenuIdFault_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new BadText exception. */
	private void throwBadText(final String message) throws BadTextFault_Exception {
		BadTextFault faultInfo = new BadTextFault();
		faultInfo.message = message;
		throw new BadTextFault_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new InsufficientQuantity exception. */
	private void throwBadQuantity(final String message) throws BadQuantityFault_Exception {
		BadQuantityFault faultInfo = new BadQuantityFault();
		faultInfo.message = message;
		throw new BadQuantityFault_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new InsufficientQuantity exception. */
	private void throwInsufficientQuantity(final String message) throws InsufficientQuantityFault_Exception {
		InsufficientQuantityFault faultInfo = new InsufficientQuantityFault();
		faultInfo.message = message;
		throw new InsufficientQuantityFault_Exception(message, faultInfo);
	}
}
