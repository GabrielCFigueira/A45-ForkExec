package com.forkexec.hub.domain.exceptions;


public class InvalidMoneyException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidMoneyException(String message) {
        super(message);
    }

}
