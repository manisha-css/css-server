package com.cynosure.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
  private Instant timestamp;
  private String message;
  private String details;

  public ErrorResponse(Instant timestamp, String message, String details) {
    this.timestamp = timestamp;
    this.message = message;
    this.details = details;
  }
}
