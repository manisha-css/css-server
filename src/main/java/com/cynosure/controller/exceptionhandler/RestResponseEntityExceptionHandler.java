package com.cynosure.controller.exceptionhandler;

import com.cynosure.dto.ErrorResponse;
import com.cynosure.exception.CssAppRuntimeException;
import java.lang.invoke.MethodHandles;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger CHILD_LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  @Autowired private Environment environment;

  // handle application exception
  @ExceptionHandler({CssAppRuntimeException.class})
  public ResponseEntity<ErrorResponse> handleCssAppRuntimeException(
      CssAppRuntimeException ex, WebRequest request) {
    CHILD_LOGGER.error("CssAppRuntimeException {}", ex);
    ErrorResponse error =
        new ErrorResponse(Instant.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // handle all other exceptions
  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
    CHILD_LOGGER.error("Generic Exception {} ", ex);
    ErrorResponse error =
        new ErrorResponse(Instant.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
