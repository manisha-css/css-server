package com.cynosure.service.impl;

import com.cynosure.dto.UserDto;
import com.cynosure.exception.CssAppRuntimeException;
import com.cynosure.exception.DBRecordNotFoundException;
import com.cynosure.model.Role;
import com.cynosure.model.User;
import com.cynosure.repo.UserRepo;
import com.cynosure.service.IEmailNotificationService;
import com.cynosure.service.IUserService;
import com.cynosure.util.CssConstant;
import com.cynosure.util.UtilityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {

  @Autowired UserRepo userRepo;

  @Autowired PasswordEncoder passwordEncoder;

  @Autowired IEmailNotificationService notificationService;

  @Autowired UtilityService utilityService;

  @Override
  public long saveUser(UserDto userDto) throws IOException {
    User user = utilityService.convertToUserBo(userDto);
    // encrypt password and set to entity
    String hashedPassword = passwordEncoder.encode(userDto.getUserPassword());
    user.setPassword(hashedPassword);
    // set USER role
    Set<Role> roles = new HashSet<>();
    roles.add(Role.ROLE_USER);
    user.setRoles(roles);
    // set verification code
    int code = 10000 + new Random().nextInt(90000);
    user.setVerificationCode(Integer.toString(code));
    user.setAccountLocked(true);

    User userSaved = userRepo.save(user);

    // add into notification
    notificationService.createNotificationForVerification(userDto);

    return userSaved.getId();
  }

  @Override
  public UserDto findUserByIdElseException(long userId) {
    return utilityService.convertToUserDto(
        userRepo
            .findById(userId)
            .orElseThrow(() -> new DBRecordNotFoundException(CssConstant.RECORD_NOTFOUND)));
  }

  @Override
  public UserDto findUserByIdElseNull(long userId) {
    return utilityService.convertToUserDto(userRepo.findById(userId).orElse(null));
  }

  @Override
  public UserDto findValidUserByIdElseNull(long userId) {
    return utilityService.convertToUserDto(
        userRepo
            .findById(userId)
            .orElseThrow(() -> new DBRecordNotFoundException(CssConstant.RECORD_NOTFOUND)));
  }

  @Override
  public UserDto findValidUserByIdElseException(long userId) {
    return utilityService.convertToUserDto(userRepo.findById(userId).orElse(null));
  }

  @Override
  public UserDto findUserByEmailElseException(String email) {
    User user =
        userRepo
            .findByUserName(email)
            .orElseThrow(() -> new DBRecordNotFoundException("User email is not found"));
    if (user != null) {
      return utilityService.convertToUserDto(user);
    }
    throw new DBRecordNotFoundException();
  }

  @Override
  public UserDto findUserByEmailElseNull(String email) {
    User user = userRepo.findByUserName(email).orElse(null);
    if (user != null) {
      return utilityService.convertToUserDto(user);
    }
    return null;
  }

  @Override
  public boolean checkEqualityOfPasswordWithDb(String email, String userEnteredPassword) {
    return passwordEncoder.matches(
        userEnteredPassword, this.findUserByEmailElseException(email).getUserPassword());
  }

  @Override
  public void verifyUser(long userId) {
    long result = userRepo.verifyUser(userId);
    if (result <= 0) {
      throw new CssAppRuntimeException("Some error in updating user during verification");
    }
  }

  @Override
  public boolean verifyUserViaCode(UserDto userDto) {
    User dbUser =
        userRepo
            .findByUserNameAndVerificationCode(
                userDto.getUserName(), userDto.getUserVerificationCode())
            .orElse(null);
    if (dbUser != null) {
      verifyUser(dbUser.getId());
      return true;
    }
    return false;
  }

  @Override
  public void verificationCodeResend(UserDto userDto) throws JsonProcessingException {
    // set verification code
    int code = 10000 + new Random().nextInt(90000);
    userDto.setUserVerificationCode(Integer.toString(code));
    // save user verification code
    long result =
        userRepo.updateVerificationCode(userDto.getUserName(), userDto.getUserVerificationCode());
    if (result <= 0) {
      throw new CssAppRuntimeException("Some error in updating user verification code");
    }

    // add into notification
    notificationService.createNotificationForVerification(userDto);
  }

  @Override
  public String updateUserWithNewPassword(long userId, String password, boolean genratePassword) {
    // validation
    if (!genratePassword && password == null) {
      throw new CssAppRuntimeException("Password can not null for change password");
    }

    String newPassword = genratePassword ? UtilityService.generateRandomPassword() : password;
    String hashedPassword = passwordEncoder.encode(newPassword);
    long result = userRepo.updateNewPassword(userId, hashedPassword);
    if (result <= 0) {
      throw new CssAppRuntimeException("Some error in updating user during update password");
    }
    return newPassword;
  }

  @Override
  public void updateUserProfile(UserDto userDto) {

    long result = userRepo.updateUserProfile(userDto.getId(), userDto.getPublicProfile());
    if (result <= 0) {
      throw new CssAppRuntimeException("Some error in updating user during update user profile");
    }
  }
}
