package com.cynosure.dto;

import com.cynosure.model.Role;
import com.cynosure.util.CssConstant;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class BasicUserDto {

  private long id;

  @NotBlank(groups = {RegisterUserValidation.class, MyProfileValidation.class})
  @Length(
      max = CssConstant.INPUTTEXT_MAXLENGTH_REGULAR,
      groups = {RegisterUserValidation.class, MyProfileValidation.class})
  @Pattern(
      regexp = CssConstant.ALPHANUMERIC_PATTERN_WITHSPACE,
      message = CssConstant.MESSAGE_INVALID_PATTERN,
      groups = {RegisterUserValidation.class, MyProfileValidation.class})
  private String givenName;

  @NotBlank(
      groups = {
        RegisterUserValidation.class,
        UserNameValidation.class,
        ChangePasswordValidation.class
      })
  @Length(
      max = CssConstant.INPUTTEXT_MAXLENGTH_REGULAR,
      groups = {
        RegisterUserValidation.class,
        UserNameValidation.class,
        ChangePasswordValidation.class
      })
  @Pattern(
      regexp = CssConstant.EMAIL_PATTERN,
      message = CssConstant.MESSAGE_INVALID_PATTERN,
      groups = {
        RegisterUserValidation.class,
        UserNameValidation.class,
        ChangePasswordValidation.class
      })
  private String userName;

  private Set<Role> roles;

  @Length(
      max = CssConstant.TEXTAREA_MAXLENGTH_REGULAR,
      groups = {MyProfileValidation.class})
  @Pattern(
      regexp = CssConstant.ALPHANUMERIC_SOMESPECIALCHARS_PATTERN,
      message = CssConstant.MESSAGE_INVALID_PATTERN,
      groups = {MyProfileValidation.class})
  private String publicProfile = "No public profile is set";

  // different interfaces for different type of form validations
  public interface RegisterUserValidation {}

  public interface UserNameValidation {}

  public interface ChangePasswordValidation {}

  public interface MyProfileValidation {}
}
