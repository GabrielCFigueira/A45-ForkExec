package com.forkexec.hub.domain.exceptions;


public class NotEnoughPointsException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotEnoughPointsException(String message) {
        super(message);
    }

}