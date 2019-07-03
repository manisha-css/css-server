package com.cynosure.util;

public class CssConstant {

  public static final String ECOKRYPT_NOTIFICATIONEMAIL_PREFIX =
      "EmailNotification from EcoKrypt.com: ";
  // scheduler limit
  public static final int SCHEDULER_RECORDLIMIT = 10;
  // login and signup and user related
  public static final String TOKEN_PREFIX = "Bearer";
  public static final String HEADER_STRING = "Authorization";
  public static final String DEFAULT_PUBLIC_PROFILE = "No public profile is set.";
  // messages to user
  public static final String RECORD_NOTFOUND = "Record not found";
  // user input related
  public static final int TEXTAREA_MAXLENGTH_REGULAR = 1000;
  public static final int PASSWORD_MINLENGTH = 8;
  public static final int PASSWORD_MAXLENGTH = 16;
  public static final int INPUTTEXT_MAXLENGTH_REGULAR = 100;
  // regex validation patters
  public static final String EMAIL_PATTERN =
      "[a-z0-9!#$%&\\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\\'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
  public static final String PHONE_PATTERN =
      "[(]([1-9][0-9]{2})[)][-]([0-9]{3})[-]([0-9]{4})"; // the form of 847-403-3160 not used
  public static final String SECRETKEY_PATTERN =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9\\\\s]).{8,16}$";
  public static final String ALPHA_PATTERN_WITHSPACE = "^[A-Za-z]{1,}[A-Za-z\\s]{0,}$";
  public static final String ALPHANUMERIC_PATTERN_WITHSPACE = "^[A-Za-z]{1,}[A-Za-z0-9\\s]{0,}$";
  public static final String TITLE_PATTERN_WITHSPACE = "^[\\s\\S]{0,}$";
  public static final String ALPHANUMERIC_SOMESPECIALCHARS_PATTERN = TITLE_PATTERN_WITHSPACE;
  public static final String CODE_PATTERN = "^[A-Za-z]{1,}[A-Za-z0-9-_]{2,17}$";
  public static final String MESSAGE_INVALID_PATTERN = "Invalid Pattern";
  public static final String ERROR_MESSAGE_BO_TO_DTO_CONVERSIONERROR =
      "Error while converting entity to dto";

  private CssConstant() {}
}
