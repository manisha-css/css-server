package com.cynosure.controller;

import com.cynosure.dto.ContactUsDto;
import com.cynosure.dto.InfoResponse;
import com.cynosure.service.IContactUsService;
import com.cynosure.service.IEmailNotificationService;
import com.cynosure.util.Translator;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Instant;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contactus")
public class ContactUsController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsController.class);

  @Autowired IContactUsService contactUsService;

  @Autowired IEmailNotificationService notificationService;

  @Autowired private Translator translator;

  @PostMapping
  public ResponseEntity<InfoResponse> saveContactUs(@Valid @RequestBody ContactUsDto contactUsDto) {
    InfoResponse infoResponse = new InfoResponse();
    contactUsDto.setCreatedOn(Instant.now());
    contactUsService.save(contactUsDto);
    try {
      notificationService.createNotificationForContactusFeedback(contactUsDto);
    } catch (JsonProcessingException e) {
      // nothing much can be done if email fails for contactus
      LOGGER.error("JsonProcessingException during teacher html file repo " + e.getMessage(), e);
    }
    infoResponse.setMessage(translator.toLocale("contactus.success", null));
    return new ResponseEntity<>(infoResponse, HttpStatus.OK);
  }
}
