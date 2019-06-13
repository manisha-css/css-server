package com.cynosure.util;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Translator {

  @Autowired MessageSource messageSource;

  public String toLocale(String msgCode, Object[] params) {
    Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(msgCode, params, locale);
  }
}
