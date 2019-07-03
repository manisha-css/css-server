package com.cynosure.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfoResponse<T> {
  private String message;
  private Instant timestamp;
  private String details;
  private T result;
}
