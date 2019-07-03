package com.cynosure.dto;

import com.cynosure.util.CssConstant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserDto extends BasicUserDto {

  @NotBlank(groups = {RegisterUserValidation.class})
  @Length(
      min = CssConstant.PASSWORD_MINLENGTH,
      max = CssConstant.PASSWORD_MAXLENGTH,
      groups = {RegisterUserValidation.class})
  @Pattern(
      regexp = CssConstant.SECRETKEY_PATTERN,
      message = CssConstant.MESSAGE_INVALID_PATTERN,
      groups = {RegisterUserValidation.class})
  private String userPassword; // this is the password to be used coming form user as input

  private String
      userVerificationCode; // this is the verification code to be used coming form user as input

  @NotBlank(groups = {ChangePasswordValidation.class})
  private String oldPassword;

  @NotBlank(groups = {ChangePasswordValidation.class})
  @Length(
      min = CssConstant.PASSWORD_MINLENGTH,
      max = CssConstant.PASSWORD_MAXLENGTH,
      groups = {ChangePasswordValidation.class})
  @Pattern(
      regexp = CssConstant.SECRETKEY_PATTERN,
      message = CssConstant.MESSAGE_INVALID_PATTERN,
      groups = {ChangePasswordValidation.class})
  private String newPassword;

  @NotBlank(groups = {RegisterUserValidation.class, ChangePasswordValidation.class})
  @Length(
      min = CssConstant.PASSWORD_MINLENGTH,
      max = CssConstant.PASSWORD_MAXLENGTH,
      groups = {RegisterUserValidation.class, ChangePasswordValidation.class})
  @Pattern(
      regexp = CssConstant.SECRETKEY_PATTERN,
      message = CssConstant.MESSAGE_INVALID_PATTERN,
      groups = {RegisterUserValidation.class, ChangePasswordValidation.class})
  private String confirmPassword;

  private boolean accountExpired;
  private boolean accountLocked; // used during verification of account
  private boolean credentialsExpired;
  private boolean accountDisabled;

  @NotNull(groups = RegisterUserValidation.class)
  private boolean acceptTC = false;
}
