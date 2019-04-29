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
	private final Map<String, Balance> user_points = new ConcurrentHashMap<>();

	// Singleton -------------------------------------------------------------

	/**
	 * Private constructor prevents instantiation from other classes.
	 */
	private Points() {
	}

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

	public static void changeStartPoints(int startPoints) throws InvalidNumberOfPointsException {
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
		Balance b = new Balance();
		b.setPoints(initialBalance.get());
		user_points.put(userId, b);
	}

	public Balance getBalance(String userId) throws EmailIsNotRegisteredException, InvalidEmailAddressException {
		if(userId == null || isValidEmailAddress(userId) == false) {
			throw new InvalidEmailAddressException(userId);
		}
		Balance res = user_points.get(userId);
		if(res == null) {
			throw new EmailIsNotRegisteredException(userId);
		}
		return res;
	}

	public void setBalance(String userId, int points, int version) throws EmailIsNotRegisteredException, InvalidEmailAddressException, InvalidNumberOfPointsException {
		if(userId == null || isValidEmailAddress(userId) == false) {
			throw new InvalidEmailAddressException(userId);
		}
		if(points < 0) {
			throw new InvalidNumberOfPointsException(points);
		}
		Balance b;
		synchronized(b = user_points.computeIfAbsent(userId, k -> new Balance())) {
			b.setPoints(points);
			b.setSeq(version);
		}
	}


	// Verifiers ------------------------------------------------------------

	public static boolean isValidEmailAddress(String email) {
		if(email == null)
			return false;
		return Pattern.matches("[a-zA-Z0-9]+([.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([.][a-zA-Z0-9]+)*", email);
	}

}
