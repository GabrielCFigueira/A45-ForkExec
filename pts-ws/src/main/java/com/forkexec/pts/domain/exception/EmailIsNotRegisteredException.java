package com.forkexec.pts.domain.exception;

public class EmailIsNotRegisteredException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmailIsNotRegisteredException(String message) {
		super(String.format("Email '%s' is not registered", message));
	}

}