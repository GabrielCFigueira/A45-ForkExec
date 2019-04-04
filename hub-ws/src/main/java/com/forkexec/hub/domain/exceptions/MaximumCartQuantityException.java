package com.forkexec.hub.domain.exceptions;


public class MaximumCartQuantityException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public MaximumCartQuantityException(String message) {
        super(message);
    }
    
}