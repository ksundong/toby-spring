package dev.idion.springbook.user.sqlservice.exception;

public class SqlNotFoundException extends RuntimeException {

  public SqlNotFoundException(String message) {
    super(message);
  }

  public SqlNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
