package com.forkexec.hub.domain;


import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;


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

	public void addUser(String userId) {
		_users.put(userId, new User(userId));
	}

	public void clearCart(String userId) {
		_users.get(userId).clearCart();
	}

	public boolean hasUser(String userId) {
		return _users.get(userId) == null;
	}

	public void addFood(String userId, FoodId foodId, int quantity) {
		_users.get(userId).addFood(foodId, quantity);
	}

	public User getUser(String userId) {
		return _users.get(userId);
	}
}
