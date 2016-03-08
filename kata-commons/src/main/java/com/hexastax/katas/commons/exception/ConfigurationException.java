package com.hexastax.katas.commons.exception;

/**
 * Thrown on a configuration error.
 * 
 * @author dgoldenberg
 */
public class ConfigurationException extends CodeKataException {

	private static final long serialVersionUID = 915209749355804817L;

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
