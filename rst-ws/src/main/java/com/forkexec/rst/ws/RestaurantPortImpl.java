package com.forkexec.rst.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import com.forkexec.rst.domain.Restaurant;
import com.forkexec.rst.domain.RestaurantMenu;
import com.forkexec.rst.domain.RestaurantMenuOrder;

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
		RestaurantMenu menu = Restaurant.getInstance().getMenu(menuId.getId());
		return newMenu(menu);
	}
	
	@Override
	public List<Menu> searchMenus(String descriptionText) throws BadTextFault_Exception {
		Restaurant rest = Restaurant.getInstance();
		Set<String> list = rest.getMenusIDs();
		List<Menu> newMenuList = new ArrayList<>();
		
		if(descriptionText.equals("%")) {
			
			for(String id: list) {
				RestaurantMenu menu = rest.getMenu(id);
				newMenuList.add(newMenu(menu));
			}
			
			return newMenuList;
			
		}else if(rest.availableString(descriptionText)) {
			return null;
		}else {
			
			for(String id: list) {
				RestaurantMenu menu = rest.getMenu(id);
				if(descriptionText.equals(menu.getEntree()) || descriptionText.equals(menu.getPlate()) || descriptionText.equals(menu.getDessert()))
					newMenuList.add(newMenu(menu));
			}
			
			if(newMenuList.size()==0)
				return null;
				
			return newMenuList;
		}
	}

	@Override
	public MenuOrder orderMenu(MenuId arg0, int arg1) throws BadMenuIdFault_Exception, BadQuantityFault_Exception, InsufficientQuantityFault_Exception {
		RestaurantMenuOrder menu =  Restaurant.getInstance().orderMenu(arg0.getId(), arg1);
		return newMenuOrder(menu);
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
		Restaurant rest = Restaurant.getInstance();
		rest.reset();
		
		for(MenuInit menu: initialMenus) {
			Menu restMenu = menu.getMenu();
			
			rest.newMenu(menu.getMenu().getId().getId(), menu.getMenu().getEntree(), menu.getMenu().getPlate(), menu.getMenu().getDessert(), menu.getMenu().getPrice(), menu.getMenu().getPreparationTime());
			rest.orderMenu(menu.getMenu().getId().getId(), menu.getQuantity());
			
		}
	}

	// View helpers ----------------------------------------------------------
	
	private Menu newMenu(RestaurantMenu menu) {
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
	
	private MenuOrder newMenuOrder(RestaurantMenuOrder menu) {
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
	private void throwInsufficientQuantity(final String message) throws InsufficientQuantityFault_Exception {
		InsufficientQuantityFault faultInfo = new InsufficientQuantityFault();
		faultInfo.message = message;
		throw new InsufficientQuantityFault_Exception(message, faultInfo);
	}
}
