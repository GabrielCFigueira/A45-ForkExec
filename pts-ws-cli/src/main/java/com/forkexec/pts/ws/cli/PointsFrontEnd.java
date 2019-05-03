package com.forkexec.pts.ws.cli;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import com.forkexec.pts.ws.cli.exception.*;
import com.forkexec.pts.ws.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.*;

/**
 * A class to implement QC protocol using the PointsClient for communication
 * with the server
 */
public class PointsFrontEnd {

	private PointsFrontEndEndPointManager endpoint;
	
	private static int majority;

	/** Locking mechanism, per user, with read and write locks (allows multiple reads, and only 1 write) */
	private ConcurrentMap<String, ReadWriteLock> user_lock;
	/** Caching mechanism, valid because no need for cache invalidation (1 client only) */
	private ConcurrentMap<String, TaggedBalance> user_cache;

	public PointsFrontEnd(int num, String UDDIUrl){
		majority = (num / 2) + 1;
		try {
			endpoint = new PointsFrontEndEndPointManager(UDDIUrl);
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}

		user_lock = new ConcurrentHashMap<>();
		user_cache = new ConcurrentHashMap<>();
	}

	public void activateUser(String userEmail) throws InvalidEmailAddressException, EmailAlreadyRegisteredException {
		if(userEmail == null)
			throw new InvalidEmailAddressException(userEmail);
		
		if(user_lock.containsKey(userEmail))
			throw new EmailAlreadyRegisteredException(userEmail);

		Lock read_lock = user_lock.get(userEmail).readLock();
		read_lock.lock();

		// Check if email is valid, and adds value to cache
		read(userEmail);
		read_lock.unlock();

	}

	public int pointsBalance(String userEmail) throws InvalidEmailAddressException, EmailIsNotRegisteredException {
		if(userEmail == null)
			throw new InvalidEmailAddressException(userEmail);
		
		user_lock.putIfAbsent(userEmail, new ReentrantReadWriteLock());
		Lock read_lock = user_lock.get(userEmail).readLock();
		read_lock.lock();
		int balance = read(userEmail).getPoints();
		read_lock.unlock();
		return balance; 

	}

	public int addPoints(String userEmail, int pointsToAdd)
			throws InvalidEmailAddressException, InvalidNumberOfPointsException, EmailIsNotRegisteredException {
		if(userEmail == null)
			throw new InvalidEmailAddressException(userEmail);

		user_lock.putIfAbsent(userEmail, new ReentrantReadWriteLock());
		Lock write_lock = user_lock.get(userEmail).writeLock();
		write_lock.lock();

		TaggedBalance balance = read(userEmail);

		balance.setPoints(pointsToAdd + balance.getPoints());
		balance.setTag(balance.getTag() + 1);

		write(userEmail, balance);

		write_lock.unlock();

		return balance.getPoints();
	}

	public int spendPoints(String userEmail, int pointsToSpend) throws InvalidEmailAddressException,
			InvalidNumberOfPointsException, EmailIsNotRegisteredException, NotEnoughPointsException {
		if(userEmail == null)
			throw new InvalidEmailAddressException(userEmail);

		user_lock.putIfAbsent(userEmail, new ReentrantReadWriteLock());
		Lock write_lock = user_lock.get(userEmail).writeLock();
		write_lock.lock();

		TaggedBalance balance = read(userEmail);
		if (pointsToSpend < 0)
			throw new InvalidNumberOfPointsException(pointsToSpend);
		if (balance.getPoints() - pointsToSpend < 0)
			throw new NotEnoughPointsException(pointsToSpend, balance.getPoints());

		balance.setPoints(balance.getPoints() - pointsToSpend);
		balance.setTag(balance.getTag() + 1);

		write(userEmail, balance);

		write_lock.unlock();

		return balance.getPoints();
	}

	// control operations -----------------------------------------------------

	public String ctrlPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";

		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append("FrontEnd");

		// FIXME try catch
		for (PointsClient client : getPointsClients()) {
			try {
				builder.append("\n").append(client.ctrlPing("points client"));
			} catch (RuntimeException e) {
				/* move on */
			}
		}
		return builder.toString();
	}

	public void ctrlClear() {
		for (PointsClient client : getPointsClients()) {
			try {
				client.ctrlClear();
			} catch (RuntimeException e) {
				/* move on */
			}
		}
		user_lock = new ConcurrentHashMap<>();
		user_cache = new ConcurrentHashMap<>();
	}

	public void ctrlInit(int startPoints) throws BadInitFault_Exception {
		for (PointsClient client : getPointsClients()) {
			try {
				client.ctrlInit(startPoints);
			} catch (RuntimeException e) {
				/* move on */
			}
		}
	}

	public void ctrlFail(int server, String failString) {
		try {
			PointsClient p = new PointsClient(endpoint.getUDDIUrl(), String.format("A45_Points%d", server));
			p.ctrlFail(failString);
		} catch (PointsClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<PointsClient> getPointsClients() {
		List<PointsClient> res = new ArrayList<PointsClient>();

		try {
			for(UDDIRecord r: endpoint.listRecords("A45_Points%")) {
				try {
					PointsClient p = new PointsClient(endpoint.getUDDIUrl(), r.getOrgName());
					res.add(p);
				} catch (PointsClientException e) {
					/* let qc take care of this*/
				}
			}
		} catch (UDDINamingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return res;
	}
	
	// Aux --------------------------------------------------------------------
	
	/** QC read. Uses value from cache, and if not, then it performs QC and puts value in cache */
	public TaggedBalance read(String userEmail) throws InvalidEmailAddressException {

		if(user_cache.containsKey(userEmail))
			return user_cache.get(userEmail);

		List<PointsClient> clients = getPointsClients();
		
		if(clients.size() < majority)
			throw new RuntimeException();

		List<Response<GetBalanceResponse>> responses = new ArrayList<Response<GetBalanceResponse>>();
		
		for(PointsClient client : clients)
			responses.add(client.getBalanceAsync(userEmail));

		int nResponses = 0;
		TaggedBalance balance = new TaggedBalance();
		balance.setTag(-1);
		while(nResponses < majority) {
			if(responses.isEmpty()) throw new RuntimeException("Not enough servers for quorum consensus");
			for(int i=responses.size()-1; i >= 0; --i) {
				if(responses.get(i).isDone()) {
					TaggedBalance newTag = new TaggedBalance();
					newTag.setTag(-1);
					try {
						newTag = responses.get(i).get().getReturn();
						nResponses++;
					} catch (InterruptedException e) {
						
					} catch (ExecutionException e) {
						if(e.getCause() instanceof InvalidEmailFault_Exception)
							throw new InvalidEmailAddressException(userEmail);
					}
					if(newTag.getTag() > balance.getTag()) {
						balance = newTag;
					}
					
					responses.remove(i);
				}
			}
		}
		
		user_cache.put(userEmail, balance);
		return balance;
		
	}
	
	/** QC write. Also updates the cache */
	public boolean write(String userEmail, TaggedBalance balance) throws InvalidEmailAddressException, InvalidNumberOfPointsException {

		List<PointsClient> clients = getPointsClients();
		
		if(clients.size() < majority)
			throw new RuntimeException();

		List<Response<SetBalanceResponse>> responses = new ArrayList<Response<SetBalanceResponse>>();
				
		for(PointsClient client : clients)
			responses.add(client.setBalanceAsync(userEmail, balance));
		
		int nResponses = 0;
		while(nResponses < majority) {
			if(responses.isEmpty()) throw new RuntimeException();
			for(int i=responses.size()-1; i >= 0; --i) {
				if(responses.get(i).isDone()) {
					try {
						responses.get(i).get();
						nResponses++;
					} catch (InterruptedException e) {
						throw new RuntimeException(e.getMessage());
					} catch (ExecutionException e) {
						if(e.getCause() instanceof InvalidEmailFault_Exception)
							throw new InvalidEmailAddressException(userEmail);
						else if(e.getCause() instanceof InvalidPointsFault_Exception)
							throw new InvalidNumberOfPointsException(balance.getPoints());
					}
					responses.remove(i);
				}
			}
		}
		
		user_cache.put(userEmail, balance);
		return true;
	}

}
