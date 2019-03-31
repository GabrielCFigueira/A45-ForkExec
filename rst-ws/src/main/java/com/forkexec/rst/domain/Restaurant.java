package com.forkexec.rst.domain;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
	
	public Boolean availableString(String text) {
		if("".equals(text) || text.contains(" "))
			return false;
		
		return true;
	}
	
	/* ------------------ MENU ------------------ */
	
	public Set<String> getMenusIDs() {
		return _menus.keySet();
	}
	
	public RestaurantMenu getMenu(String menuId){
		return _menus.get(menuId);
	}
	
	public synchronized void setMenu(RestaurantMenu menu) {
		_menus.put(menu.getId(),menu);
	}
	
	public Boolean acceptMenu(String menuId, String entree, String plate, String dessert, int price, int preparationTime) {
		return availableString(menuId) && availableString(entree) && availableString(plate) && availableString(dessert) && (price > 0) && (preparationTime > 0);
	}
	
	public synchronized void newMenu(String menuId, String entree, String plate, String dessert, int price, int preparationTime) {
		if(acceptMenu(menuId, entree, plate, dessert, price, preparationTime))
			_menus.put(menuId, new RestaurantMenu(menuId, entree, plate, dessert, price, preparationTime));
	}
	
	/* --------------- MENU ORDER --------------- */
	
	public Set<String> getMenuOrdersIDs() {
		return _orders.keySet();
	}
	
	public RestaurantMenuOrder getMenuOrder(String menuId){
		return _orders.get(menuId);
	}
	
	public synchronized void setMenuOrder(RestaurantMenuOrder menu) {
		_orders.put(menu.getId(),menu);
	}
	
	public Boolean acceptMenuOrder(String orderId, String menuId, int menuQuantity) {
		return availableString(orderId) && availableString(menuId) && (menuQuantity > 0);
	}
	
	public synchronized void newMenuOrder(String orderId, String menuId, int menuQuantity) {
		if(acceptMenuOrder(orderId, menuId, menuQuantity))
			_orders.put(orderId, new RestaurantMenuOrder(orderId, menuId, menuQuantity));
	}
	
	public synchronized RestaurantMenuOrder orderMenu(String menuId, int qty) {
		Integer orderIdCounter = _orderIdCounter.incrementAndGet();
		if(acceptMenuOrder(orderIdCounter.toString(), menuId, qty))
			return new RestaurantMenuOrder(orderIdCounter.toString(), menuId, qty);
		return null;
	}
	
}
