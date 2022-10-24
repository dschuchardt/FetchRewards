package com.fetch;

public class NotSpendException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7490832998514552712L;

	public NotSpendException() {
		super("Could not complete transaction: Attempting to add points to balance via spend method");
	}

}
