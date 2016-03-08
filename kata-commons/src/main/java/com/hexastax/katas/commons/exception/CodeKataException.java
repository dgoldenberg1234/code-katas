package com.hexastax.katas.commons.exception;

/**
 * Represents the base class for all code kata exceptions.
 * 
 * @author dgoldenberg
 */
public class CodeKataException extends Exception {

  private static final long serialVersionUID = 6452595447361619202L;

  public CodeKataException(String message) {
    super(message);
  }

  public CodeKataException(Throwable cause) {
    super(cause);
  }

  public CodeKataException(String message, Throwable cause) {
    super(message, cause);
  }
}
