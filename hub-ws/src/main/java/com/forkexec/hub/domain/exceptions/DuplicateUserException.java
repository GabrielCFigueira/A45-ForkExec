package com.forkexec.hub.domain.exceptions;


public class DuplicateUserException extends Exception {


    public DuplicateUserException(String message) {
        super(message);
    }

}