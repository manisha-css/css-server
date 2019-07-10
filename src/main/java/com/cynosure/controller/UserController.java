package com.cynosure.controller;

import com.cynosure.dto.BasicUserDto;
import com.cynosure.dto.InfoResponse;
import com.cynosure.dto.UserDto;
import com.cynosure.security.model.CustomUserDetails;
import com.cynosure.service.IEmailNotificationService;
import com.cynosure.service.IUserService;
import com.cynosure.util.CssConstant;
import com.cynosure.util.Translator;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
  private static final String USER_NOTFOUND = "manage-user.user.notfound";
  private static final String USERNAME_EXISTS = "register-user.username-exists";

  @Autowired private IUserService userService;
  @Autowired private Translator translator;
  @Autowired private IEmailNotificationService notificationService;

  @PostMapping("/register")
  public ResponseEntity<InfoResponse> registerUser(
      @Validated(BasicUserDto.RegisterUserValidation.class) @RequestBody UserDto userDto) {
    InfoResponse infoResponse = new InfoResponse();
    if (!userDto.isAcceptTC()) {
      infoResponse.setMessage(translator.toLocale("register-user.accept-tc.error", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.PRECONDITION_REQUIRED);
    }
    // check if confirm password is not same as password
    if (!userDto.getUserPassword().equals(userDto.getConfirmPassword())) {
      infoResponse.setMessage(
          translator.toLocale("register-user.password.cnfpassword.mismatch", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.PRECONDITION_REQUIRED);
    }
    // Please note that due to pattern validation userName will always be in lower case
    UserDto newuserDto = userService.findUserByEmailElseNull(userDto.getUserName());
    // check for duplicate user name (email)
    if (newuserDto != null) {
      if (newuserDto.isAccountLocked()) {
        infoResponse.setMessage(translator.toLocale("register-user.username-exists-locked", null));
        return new ResponseEntity<>(infoResponse, HttpStatus.CONFLICT);
      }
      if (newuserDto.isAccountDisabled()) {
        infoResponse.setMessage(
            translator.toLocale("register-user.username-exists-disabled", null));
        return new ResponseEntity<>(infoResponse, HttpStatus.CONFLICT);
      }
      infoResponse.setMessage(translator.toLocale(USERNAME_EXISTS, null));
      return new ResponseEntity<>(infoResponse, HttpStatus.CONFLICT);
    }
    return saveUserDto(userDto);
  }

  @PostMapping("/resendVerificationlink")
  public ResponseEntity<InfoResponse> resendVerificationLink(
      @Validated(BasicUserDto.UserNameValidation.class) @RequestBody UserDto requserDto) {
    UserDto userDto = userService.findUserByEmailElseNull(requserDto.getUserName());
    InfoResponse infoResponse = new InfoResponse();
    // here exception is not thrown as want to display different message.
    if (userDto == null) {
      infoResponse.setMessage(translator.toLocale(USER_NOTFOUND, null));
      return new ResponseEntity<>(infoResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    if (!userDto.isAccountLocked()) {
      infoResponse.setMessage(translator.toLocale("verify-user.alreadyverified.error", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.BAD_REQUEST);
    }
    // i.e. if user is not null and locked)
    try {
      notificationService.createNotificationForVerification(userDto);
      infoResponse.setMessage(translator.toLocale("resend-verification-link.save.success", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.OK);
    } catch (JsonProcessingException jsonEx) {
      LOGGER.error("Issue is saving in notification table: {}", jsonEx);
      infoResponse.setMessage(translator.toLocale("resend-verification-link.save.error", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/forgetPassword")
  public ResponseEntity<InfoResponse> forgetPassword(
      @Validated(BasicUserDto.UserNameValidation.class) @RequestBody UserDto requserDto) {
    UserDto userDto = userService.findUserByEmailElseNull(requserDto.getUserName());
    InfoResponse infoResponse = new InfoResponse();
    if (userDto == null) {
      infoResponse.setMessage(translator.toLocale(USER_NOTFOUND, null));
      return new ResponseEntity<>(infoResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    if (userDto.isAccountLocked()) {
      infoResponse.setMessage(translator.toLocale("forget-password.nonverifieduser", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    // Currently if user is blocked - still forget password is allowed
    try {
      String newPassword = userService.updateUserWithNewPassword(userDto.getId(), null, true);
      userDto.setUserPassword(newPassword);
      notificationService.createNotificationForForgetPassword(userDto);
      infoResponse.setMessage(translator.toLocale("forget-password.save.success", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.OK);
    } catch (JsonProcessingException jsonEx) {
      LOGGER.error("Issue is saving forget password email  in notification table: {}", jsonEx);
      infoResponse.setMessage(
          translator.toLocale("forget-password.save.error", null)); // Add CssConstant.GENERAL_ERROR
      return new ResponseEntity<>(infoResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/changePassword")
  public ResponseEntity<InfoResponse> changePassword(
      @Validated(BasicUserDto.ChangePasswordValidation.class) @RequestBody UserDto requserDto) {
    UserDto userDto = userService.findUserByEmailElseNull(requserDto.getUserName());
    InfoResponse infoResponse = new InfoResponse();
    if (userDto == null) {
      infoResponse.setMessage(translator.toLocale(USER_NOTFOUND, null));
      return new ResponseEntity<>(infoResponse, HttpStatus.NOT_ACCEPTABLE);
    }
    if (!userService.checkEqualityOfPasswordWithDb(
        requserDto.getUserName(), requserDto.getOldPassword())) {
      infoResponse.setMessage(translator.toLocale("change-password.password.mismatch", null));

      return new ResponseEntity<>(infoResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    if (!requserDto.getNewPassword().equals(requserDto.getConfirmPassword())) {
      infoResponse.setMessage(
          translator.toLocale("change-password.newpassword.cnfpassword.mismatch", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    userService.updateUserWithNewPassword(userDto.getId(), requserDto.getNewPassword(), false);
    infoResponse.setMessage(translator.toLocale("change-password.save.success", null));
    return new ResponseEntity<>(infoResponse, HttpStatus.OK);
  }

  @PostMapping("/myprofile")
  public ResponseEntity<InfoResponse> saveMyProfile(
      Authentication authentication,
      @Validated(BasicUserDto.MyProfileValidation.class) @RequestBody UserDto requserDto) {
    // Here userId is assumed not null
    Long userId = ((CustomUserDetails) authentication.getDetails()).getAuthUser().getId();
    userService.findValidUserByIdElseException(userId);
    InfoResponse infoResponse = new InfoResponse();
    requserDto.setId(userId);
    if (StringUtils.isEmpty(requserDto.getPublicProfile())) {
      requserDto.setPublicProfile(CssConstant.DEFAULT_PUBLIC_PROFILE);
    }
    try {
      userService.updateUserProfile(requserDto);
      infoResponse.setMessage(translator.toLocale("myprofile.save.success", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.OK);
    } catch (DuplicateKeyException exception) { // this is kept as well in case DB throws an error
      LOGGER.error("User name exists {}", exception);
      infoResponse.setMessage(translator.toLocale(USERNAME_EXISTS, null));
      return new ResponseEntity<>(infoResponse, HttpStatus.CONFLICT);
    }
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUserById(@PathVariable long userId) {
    UserDto userDto = userService.findValidUserByIdElseException(userId);
    return new ResponseEntity<>(userDto, HttpStatus.OK);
  }

  @GetMapping("/basicuser/{userId}")
  public ResponseEntity<BasicUserDto> getBasicUser(@PathVariable long userId) {

    BasicUserDto basicUserDto = userService.findValidUserByIdElseException(userId);
    return new ResponseEntity<>(basicUserDto, HttpStatus.OK);
  }

  private ResponseEntity<InfoResponse> saveUserDto(UserDto userDto) {
    InfoResponse infoResponse = new InfoResponse();
    try {
      // save user + send notification
      // set default profile
      if (StringUtils.isEmpty(userDto.getPublicProfile())) {
        userDto.setPublicProfile(CssConstant.DEFAULT_PUBLIC_PROFILE);
      }
      long userId = userService.saveUser(userDto);
      if (userId > 0) {
        infoResponse.setMessage(translator.toLocale("register-user.save.success", null));

        return new ResponseEntity<>(infoResponse, HttpStatus.CREATED);
      } else {
        infoResponse.setMessage(
            translator.toLocale("register-user.save.error", null)); // CssConstant.GENERAL_ERROR
        return new ResponseEntity<>(infoResponse, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } catch (DuplicateKeyException exception) { // this is kept as well in case DB throws an error
      LOGGER.error("User name exists {}", exception);
      infoResponse.setMessage(translator.toLocale(USERNAME_EXISTS, null));
      return new ResponseEntity<>(infoResponse, HttpStatus.CONFLICT);
    } catch (JsonProcessingException jsonEx) {
      LOGGER.error("User saved, but there error occured while sending notification {}", jsonEx);
      infoResponse.setMessage(translator.toLocale("register-user.save.notificationerror", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.FAILED_DEPENDENCY);
    } catch (IOException ioe) {
      LOGGER.error("Profile image not stored: {} ", ioe);
      // TODO handle such cases from backend
      infoResponse.setMessage(translator.toLocale("register-user.save.imgerror", null));
      return new ResponseEntity<>(infoResponse, HttpStatus.FAILED_DEPENDENCY);
    }
  }
}
