package com.fetch;

class InsufficientBalanceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -970919954686394187L;

	InsufficientBalanceException() {
		super("Could not complete transaction: Insufficient points balance ");
	}

}
