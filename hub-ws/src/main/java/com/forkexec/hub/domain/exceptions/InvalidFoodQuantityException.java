package com.forkexec.hub.domain.exceptions;


public class InvalidFoodQuantityException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidFoodQuantityException(String message) {
        super(message);
    }

}