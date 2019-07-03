package com.cynosure.service;

import com.cynosure.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;

public interface IUserService {
  long saveUser(UserDto userDto) throws IOException;

  UserDto findUserByIdElseNull(long userId);

  UserDto findUserByIdElseException(long userId);

  UserDto findValidUserByIdElseNull(long userId);

  UserDto findValidUserByIdElseException(long userId);

  UserDto findUserByEmailElseException(String email);

  UserDto findUserByEmailElseNull(String email);

  boolean checkEqualityOfPasswordWithDb(String email, String userEnteredPassword);

  void verifyUser(long userId);

  boolean verifyUserViaCode(UserDto userDto);

  void verificationCodeResend(UserDto userDto) throws JsonProcessingException;

  String updateUserWithNewPassword(long userId, String password, boolean genratePassword);

  void updateUserProfile(UserDto userDto);
}
