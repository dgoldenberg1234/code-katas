package com.hexastax.katas.commons.exception;

/**
 * Thrown on an error having to do with data persistence.
 * 
 * @author dgoldenberg
 */
public class PersistenceException extends CodeKataException {

	private static final long serialVersionUID = 2945662082725711344L;

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}
}
