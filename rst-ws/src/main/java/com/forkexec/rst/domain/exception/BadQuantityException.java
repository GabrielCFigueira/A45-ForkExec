package com.forkexec.rst.domain.exception;

public class BadQuantityException extends Exception {

    private static final long serialVersionUID = 1L;

    public BadQuantityException(String message) {
        super(message);
    }

}