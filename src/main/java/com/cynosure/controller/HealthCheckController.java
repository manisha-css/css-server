package com.cynosure.controller;

import com.cynosure.dto.InfoResponse;
import com.cynosure.util.Translator;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {
  private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckController.class);
  @Autowired private Translator translator;

  @GetMapping
  public ResponseEntity<InfoResponse> getHealthCheck(HttpServletRequest httpServletRequest) {
    InfoResponse infoResponse = new InfoResponse();
    infoResponse.setMessage(translator.toLocale("healthcheck.ok", null));
    LOGGER.debug("Debug log message");
    LOGGER.info("Info log message");
    LOGGER.error("Error log message");
    return new ResponseEntity<>(infoResponse, HttpStatus.OK);
  }
}
