package com.fetch;

public class InvalidDateException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7560800848736134166L;

	public InvalidDateException() {
		super("Could not complete transaction: Timestamp is in the future.");
	}

}
