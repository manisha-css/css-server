package com.cynosure.exception;

public class DBRecordNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public DBRecordNotFoundException() {
    super();
  }

  public DBRecordNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public DBRecordNotFoundException(String message) {
    super(message);
  }

  public DBRecordNotFoundException(Throwable cause) {
    super(cause);
  }
}
