package com.cynosure.dto;

import com.cynosure.util.CssConstant;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class ContactUsDto {

  private String id;

  @NotBlank
  @Length(max = CssConstant.INPUTTEXT_MAXLENGTH_REGULAR)
  @Pattern(
      regexp = CssConstant.ALPHA_PATTERN_WITHSPACE,
      message = CssConstant.MESSAGE_INVALID_PATTERN)
  private String givenName;

  @NotBlank
  @Length(max = CssConstant.INPUTTEXT_MAXLENGTH_REGULAR)
  @Pattern(regexp = CssConstant.EMAIL_PATTERN, message = CssConstant.MESSAGE_INVALID_PATTERN)
  private String email;

  @NotBlank
  @Length(max = CssConstant.INPUTTEXT_MAXLENGTH_REGULAR)
  @Pattern(
      regexp = CssConstant.ALPHANUMERIC_SOMESPECIALCHARS_PATTERN,
      message = CssConstant.MESSAGE_INVALID_PATTERN)
  private String feedback;

  private Instant createdOn;
}
