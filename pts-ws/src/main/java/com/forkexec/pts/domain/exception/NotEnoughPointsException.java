package com.forkexec.pts.domain.exception;

import static java.lang.String.format;

public class NotEnoughPointsException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotEnoughPointsException(String message) {
        super(message);
    }

    public NotEnoughPointsException(int pointsToSpend, int totalPoints) {
        super(format("Cannot spend '%d' points (have only %d points total)", pointsToSpend, totalPoints));
    }
}