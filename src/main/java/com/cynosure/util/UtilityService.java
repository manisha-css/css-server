package com.cynosure.util;

import com.cynosure.controller.UserController;
import com.cynosure.dto.ContactUsDto;
import com.cynosure.dto.UserDto;
import com.cynosure.exception.CssAppRuntimeException;
import com.cynosure.model.ContactUs;
import com.cynosure.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifmif.common.regex.Generex;
import java.util.Random;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("utilityService")
public class UtilityService {

  private static final Random RANDOM = new Random();
  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  @Autowired ModelMapper commonModelMapper;
  @Autowired ModelMapper userModelMapper;

  public static String generateRandomPassword() {
    Generex generex = new Generex("[0-3]([a-c]|[e-g]{1,2})");
    // Generate random String
    return generex.random();
  }

  /*Generate random numbers within the range 48 (unicode for 0) to 57 (unicode for 9).
  0-9 = U+0048 - U+0057 = 48-57 decimal

  Stop when you have the required length of the string.
  Collect the chars produced into a StringBuilder
  Turn the StringBuilder in a String and return
  Ref: http://www.rationaljava.com/2015/06/java8-generate-random-string-in-one-line.html
  */
  public static String generateRandomNumber(int length) {
    LOGGER.debug("Random verification code generated");
    return RANDOM
        .ints(48, 58) // 58 as it is exclusive
        .limit(length)
        .collect(StringBuilder::new, (sb, i) -> sb.append((char) i), StringBuilder::append)
        .toString();
  }

  public String convertObjectToJson(Object object) throws JsonProcessingException {
    if (object == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }
  // ============Conversions==============
  // contact us
  public ContactUs convertToContactUs(ContactUsDto contactUsDto) {
    if (contactUsDto != null) {
      return commonModelMapper.map(contactUsDto, ContactUs.class);
    }
    return null;
  }

  public ContactUsDto convertToContactUsDto(ContactUs contactUsBo) {
    if (contactUsBo != null) {
      return commonModelMapper.map(contactUsBo, ContactUsDto.class);
    }
    return null;
  }

  // user
  public UserDto convertToUserDto(User userBo) {
    if (userBo != null) {
      return commonModelMapper.map(userBo, UserDto.class);
    }
    throw new CssAppRuntimeException(CssConstant.ERROR_MESSAGE_BO_TO_DTO_CONVERSIONERROR);
  }

  public User convertToUserBo(UserDto userDto) {
    if (userDto != null) {
      return userModelMapper.map(userDto, User.class);
    }
    return null;
  }
}
