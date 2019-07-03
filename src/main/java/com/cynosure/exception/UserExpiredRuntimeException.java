package com.cynosure.exception;

public class UserExpiredRuntimeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public UserExpiredRuntimeException() {
    super();
  }

  public UserExpiredRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserExpiredRuntimeException(String message) {
    super(message);
  }

  public UserExpiredRuntimeException(Throwable cause) {
    super(cause);
  }
}
