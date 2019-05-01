package com.forkexec.pts.ws.cli.exception;

public class InvalidEmailAddressException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidEmailAddressException(String email) {
        super(constructMessage(email));
    }

    private static String constructMessage(String email) {
        if(email == null) {
            return "Email cannot be null";
        } else {
            return String.format("Email '%s' is invalid", email); 
        }
    }

}