package com.forkexec.pts.domain.exception;

import static java.lang.String.format;

public class NotEnoughPoints extends Exception {

    private static final long serialVersionUID = 1L;

    public NotEnoughPoints(String message) {
        super(message);
    }

    public NotEnoughPoints(int pointsToSpend, int totalPoints) {
        super(format("Cannot spend '%d' points (have only %d points total)", pointsToSpend, totalPoints));
    }
}