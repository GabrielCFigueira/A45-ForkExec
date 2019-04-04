package com.forkexec.hub.domain;


import java.util.Map;
import java.util.TreeMap;

import com.forkexec.hub.domain.exceptions.NoSuchUserException;
import com.forkexec.hub.domain.exceptions.DuplicateUserException;
import com.forkexec.hub.domain.exceptions.MaximumCartQuantityException;
import com.forkexec.hub.domain.exceptions.InvalidFoodQuantityException;


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

	public void addUser(String userId) throws DuplicateUserException {
		if (_users.containsKey(userId))
			throw new DuplicateUserException("User with Id: " + userId + " already exists");
		else
			_users.put(userId, new User(userId));
	}

	public void clearCart(String userId) throws NoSuchUserException {
		getUser(userId).clearCart();
	}

	public void addFood(String userId, FoodId foodId, int quantity) 
		throws NoSuchUserException, MaximumCartQuantityException, InvalidFoodQuantityException {
		getUser(userId).addFood(foodId, quantity);
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
}
