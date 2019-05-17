package com.cynosure.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<String> getHealthCheck(HttpServletRequest httpServletRequest) {

    String result = "{\"result\":\"HealthOK\"}";

    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
