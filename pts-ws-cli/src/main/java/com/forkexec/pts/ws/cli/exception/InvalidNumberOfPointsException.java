package com.forkexec.pts.ws.cli.exception;

import static java.lang.String.format;

public class InvalidNumberOfPointsException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidNumberOfPointsException(String message) {
        super(message);
    }

    public InvalidNumberOfPointsException(int points) {
        super(format("'%d' is not a valid number of points", points));
    }
}