package com.forkexec.pts.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.forkexec.pts.domain.exception.*;

/**
 * Points
 * <p>
 * A points server.
 */
public class Points {

	/**
	 * Constant representing the default initial balance for every new client
	 */
	private static final int DEFAULT_INITIAL_BALANCE = 100;
	/**
	 * Global with the current value for the initial balance of every new client
	 */
	private final AtomicInteger initialBalance = new AtomicInteger(DEFAULT_INITIAL_BALANCE);
	/**
	 * Global map that holds the association between users and points
	 */
	private final Map<String, Integer> user_points = new ConcurrentHashMap<>();

	// Singleton -------------------------------------------------------------

	/**
	 * Private constructor prevents instantiation from other classes.
	 */
	private Points() { }

	private Points(int startPoints) {
		this.initialBalance.set(startPoints);
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static Points INSTANCE = new Points();

		private static void reset() {
			INSTANCE = new Points();
		}
	}

	public static synchronized Points getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public static synchronized void resetInstance() {
		SingletonHolder.reset();
	}

	public static void resetInstance(int startPoints) throws InvalidNumberOfPointsException {
		if(startPoints < 0) {
			throw new InvalidNumberOfPointsException(startPoints);
		}
		SingletonHolder.INSTANCE.setInitialBalance(startPoints);
	}

	// Getters --------------------------------------------------------------
	/**
	 * @return the initialBalance
	 */
	public int getInitialBalance() {
		return initialBalance.get();
	}

	/**
	 * balance: the balance that should be given to new Clients
	 */
	private void setInitialBalance(int balance) {
		initialBalance.set(balance);
	}

	// Functionality functions ----------------------------------------------

	public synchronized void addUser(String userId) throws EmailAlreadyRegisteredException, InvalidEmailAddressException {
		if(isValidEmailAddress(userId) == false) {
			throw new InvalidEmailAddressException(userId);
		}
		if(user_points.containsKey(userId) == true) {
			throw new EmailAlreadyRegisteredException(userId);
		}
		user_points.put(userId, initialBalance.get());
	}

	public int getPoints(String userId) throws EmailIsNotRegisteredException {
		Integer res = user_points.get(userId);
		if(res == null) {
			throw new EmailIsNotRegisteredException(userId);
		}
		return res;
	}

	public synchronized int changePoints(String userId, int delta) throws NotEnoughPoints, EmailIsNotRegisteredException {
		if(user_points.containsKey(userId) == false) {
			throw new EmailIsNotRegisteredException(userId);
		}
		if(user_points.get(userId) + delta < 0) {
			throw new NotEnoughPoints(-delta, user_points.get(userId));
		}
		return user_points.computeIfPresent(userId, (user, old_val) -> old_val + delta);
	}

	// Verifiers ------------------------------------------------------------

	public static boolean isValidEmailAddress(String email) {
		if(email == null)
			return false;
		return Pattern.matches("[a-zA-Z0-9]+([.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([.][a-zA-Z0-9]+)*", email);
	}

}
