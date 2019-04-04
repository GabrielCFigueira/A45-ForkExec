package com.forkexec.hub.domain.exceptions;


public class InvalidCreditCardException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidCreditCardException(String message) {
        super(message);
    }

}
