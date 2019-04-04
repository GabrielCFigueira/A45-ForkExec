package com.forkexec.hub.domain.exceptions;


public class InvalidFoodIdException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidFoodIdException(String message) {
        super(message);
    }

}
