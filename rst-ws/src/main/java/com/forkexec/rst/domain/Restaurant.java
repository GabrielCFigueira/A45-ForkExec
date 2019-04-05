package com.forkexec.rst.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.forkexec.rst.domain.exception.InsufficientQuantityException;
import com.forkexec.rst.domain.exception.BadQuantityException;

/**
 * Restaurant
 *
 * A restaurant server.
 *
 */
public class Restaurant {

	private Map<String, RestaurantMenu> _menus = new ConcurrentHashMap<>();
	private Map<String, RestaurantMenuOrder> _orders = new ConcurrentHashMap<>();
	
	private AtomicInteger _orderIdCounter = new AtomicInteger(0);
	
	// Singleton -------------------------------------------------------------

	/** Private constructor prevents instantiation from other classes. */
	private Restaurant() {
		// Initialization of default values
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final Restaurant INSTANCE = new Restaurant();
	}

	public static synchronized Restaurant getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public void reset(){
		_menus.clear();
		_orders.clear();
		_orderIdCounter.set(0);
	}
	
	public Boolean availableString(String text, Boolean emptyFlag) {
		if(text.isEmpty() && emptyFlag) {
			return false;
		}else if((text.isEmpty() || text.contains(" ")) && !emptyFlag) {
			return false;
		}
		
		return true;
	}
	
	/* ------------------ MENU ------------------ */
	
	public Set<String> getMenusIDs() {
		return _menus.keySet();
	}
	
	public RestaurantMenu getMenu(String menuId){
		return _menus.get(menuId);
	}
	
	public List<RestaurantMenu> getMenusByDescription(String descriptionText){
		List<RestaurantMenu> menusList = new ArrayList<RestaurantMenu>();
		for(String id: getMenusIDs()) {
			RestaurantMenu menu = getMenu(id);
			if(menu.getEntree().contains(descriptionText) || menu.getPlate().contains(descriptionText) || menu.getDessert().contains(descriptionText)) {
				menusList.add(menu);
			}
		}
		return menusList;
	}
	
	public void setMenu(RestaurantMenu menu) {
		_menus.put(menu.getId(),menu);
	}
	
	public Boolean acceptMenu(String menuId, String entree, String plate, String dessert, int price, int preparationTime, int quantity) {
		return availableString(menuId, true) && availableString(entree, true) && availableString(plate, true) && availableString(dessert, true) && (price > 0) && (preparationTime > 0) && (quantity > 0);
	}
	
	public void newMenu(String menuId, String entree, String plate, String dessert, int price, int preparationTime, int quantity) {
		if(acceptMenu(menuId, entree, plate, dessert, price, preparationTime, quantity))
			_menus.put(menuId, new RestaurantMenu(menuId, entree, plate, dessert, price, preparationTime, quantity));
	}
	
	/* --------------- MENU ORDER --------------- */
	
	public Set<String> getMenuOrdersIDs() {
		return _orders.keySet();
	}
	
	public RestaurantMenuOrder getMenuOrder(String menuId){
		return _orders.get(menuId);
	}
	
	public void setMenuOrder(RestaurantMenuOrder menu) {
		_orders.put(menu.getId(),menu);
	}
	
	public Boolean acceptMenuOrder(String orderId, String menuId, int menuQuantity) {
		return availableString(orderId, true) && availableString(menuId, true) && (menuQuantity > 0);
	}
	
	public RestaurantMenuOrder orderMenu(String menuId, int qty) throws InsufficientQuantityException, BadQuantityException {
		if(qty <= 0) {
			throw new BadQuantityException("Quantity cannot be negative or zero!");
		}
		
		Integer orderIdCounter = _orderIdCounter.incrementAndGet();
		
		if(acceptMenuOrder(orderIdCounter.toString(), menuId, qty)) {
			RestaurantMenu menu = getMenu(menuId);
			int diff = menu.getQuantity()-qty;
			
			if(menu.getId().equals(menuId) && diff >= 0) {
				menu.setQuantity(diff);
				RestaurantMenuOrder newMenuOrder = new RestaurantMenuOrder(orderIdCounter.toString(), menuId, qty);
				
				_orders.put(orderIdCounter.toString(), newMenuOrder);
				return newMenuOrder;
				
			}else {
				_orderIdCounter.decrementAndGet();
				throw new InsufficientQuantityException("Menu do not have enough quantity!");
			}
		
		}
		return null;
	}
	
}
