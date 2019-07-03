package com.cynosure.exception;

public class InvalidJWTTokenSignatureRuntimeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public InvalidJWTTokenSignatureRuntimeException() {
    super();
  }

  public InvalidJWTTokenSignatureRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidJWTTokenSignatureRuntimeException(String message) {
    super(message);
  }

  public InvalidJWTTokenSignatureRuntimeException(Throwable cause) {
    super(cause);
  }
}
