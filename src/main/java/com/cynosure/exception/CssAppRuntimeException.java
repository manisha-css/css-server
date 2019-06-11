package com.cynosure.exception;

public class CssAppRuntimeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public CssAppRuntimeException() {
    super();
  }

  public CssAppRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public CssAppRuntimeException(String message) {
    super(message);
  }

  public CssAppRuntimeException(Throwable cause) {
    super(cause);
  }
}
