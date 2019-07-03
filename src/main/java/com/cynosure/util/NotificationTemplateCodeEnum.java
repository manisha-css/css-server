package com.cynosure.util;

public enum NotificationTemplateCodeEnum {
  CREATE_USER("create-user.ftl"),
  FORGET_PASSWORD("forget-password.ftl"),
  CONTACTUS_FEEDBACK("contactus-feedback.ftl");

  private final String value;

  NotificationTemplateCodeEnum(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
