package com.cynosure.controller;

import com.cynosure.dto.InfoResponse;
import com.cynosure.dto.UserDto;
import com.cynosure.service.IUserService;
import com.cynosure.util.Translator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(
    path = "//user/verification",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
public class VerifyUserEmailViaCodeController {

  @Autowired IUserService userService;

  @Autowired private Translator translator;

  @Autowired private Environment environment;

  @PostMapping(value = "/verifyemail")
  public ResponseEntity<InfoResponse> verifyUserViaCode(@RequestBody UserDto requserDto) {
    UserDto userDto = userService.findUserByEmailElseNull(requserDto.getUserName());
    InfoResponse infoResponse = new InfoResponse();
    if (userDto == null) {
      infoResponse.setMessage(translator.toLocale("manage-user.user.notfound", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    if (!userDto
        .isAccountLocked()) { // if account is not locked that means user is already verified
      infoResponse.setMessage(translator.toLocale("verify-user.alreadyverified.error", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.BAD_REQUEST);
    }

    boolean result = userService.verifyUserViaCode(requserDto);
    if (result) {
      infoResponse.setMessage(translator.toLocale("verify-user.success", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.OK);
    } else {
      infoResponse.setMessage(translator.toLocale("verify-user.error", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping(value = "/resend")
  public ResponseEntity<InfoResponse> verificationCodeResend(@RequestBody UserDto requserDto) {
    UserDto userDto = userService.findUserByEmailElseNull(requserDto.getUserName());
    InfoResponse infoResponse = new InfoResponse();
    if (userDto == null) {
      infoResponse.setMessage(translator.toLocale("manage-user.user.notfound", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    if (!userDto
        .isAccountLocked()) { // if account is not locked that means user is already verified
      infoResponse.setMessage(translator.toLocale("verify-user.alreadyverified.error", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.BAD_REQUEST);
    }

    try {
      userService.verificationCodeResend(requserDto);
      infoResponse.setMessage(translator.toLocale("verificationcode-resend.success", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.OK);
    } catch (JsonProcessingException je) {
      infoResponse.setMessage(translator.toLocale("verificationcode-resend.error", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
