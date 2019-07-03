package com.cynosure.config;

import com.cynosure.dto.UserDto;
import com.cynosure.model.User;
import java.util.Arrays;
import java.util.Locale;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class CssMainAppConfiguration {

  @Autowired private Environment environment;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ModelMapper commonModelMapper() {
    return new ModelMapper();
  }

  @Bean
  public ModelMapper userModelMapper() {
    PropertyMap<UserDto, User> skipUserMap =
        new PropertyMap<UserDto, User>() {
          @Override
          protected void configure() {
            skip().setPassword(null);
          }
        };

    PropertyMap<User, UserDto> skipUserDtoMap =
        new PropertyMap<User, UserDto>() {
          @Override
          protected void configure() {
            skip().setUserPassword(null);
          }
        };

    ModelMapper userModelMapper = new ModelMapper();
    userModelMapper.addMappings(skipUserMap);
    userModelMapper.addMappings(skipUserDtoMap);
    return userModelMapper;
  }

  @Bean
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setPoolSize(10);
    threadPoolTaskScheduler.setThreadNamePrefix("css-scheduledtask-pool-");
    return threadPoolTaskScheduler;
  }

  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasenames("i18n/messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean("localeResolver")
  public LocaleResolver acceptHeaderLocaleResolver() {
    AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
    resolver.setDefaultLocale(Locale.US);
    resolver.setSupportedLocales(Arrays.asList(Locale.US, Locale.FRENCH));
    return resolver;
  }
}
