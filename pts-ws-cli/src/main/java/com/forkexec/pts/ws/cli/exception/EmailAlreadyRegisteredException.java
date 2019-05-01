package com.forkexec.pts.ws.cli.exception;

public class EmailAlreadyRegisteredException extends Exception {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyRegisteredException(String email) {
		super(String.format("Email '%s' was already registered", email));
    }

}