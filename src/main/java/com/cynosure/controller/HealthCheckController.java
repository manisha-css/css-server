package com.cynosure.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {

  private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckController.class);

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<String> getHealthCheck(HttpServletRequest httpServletRequest) {

    String result = "{\"result\":\"HealthOK\"}";
    LOGGER.debug("Debug log message");
    LOGGER.info("Info log message");
    LOGGER.error("Error log message");


    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
