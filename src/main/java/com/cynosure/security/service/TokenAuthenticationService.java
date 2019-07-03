package com.cynosure.security.service;

import com.cynosure.dto.AuthUser;
import com.cynosure.dto.BasicUserDto;
import com.cynosure.exception.UserExpiredRuntimeException;
import com.cynosure.model.User;
import com.cynosure.repo.UserRepo;
import com.cynosure.security.model.CustomUserAuthentication;
import com.cynosure.security.model.CustomUserDetails;
import com.cynosure.service.IUserService;
import com.cynosure.util.CssConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

public class TokenAuthenticationService {

  static final long EXPIRATIONTIME = 864_000_000; // 10 days; 1 day = 86 400 000 ms
  static final String SECRET = "EE9E02C06560478B29831EDA27FE272F05512FB26F3228D3C9E2ACBF6A391E6F";

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationService.class);
  @Autowired UserRepo userRepo;

  @Autowired IUserService userServiceImpl;

  public static String addAuthentication(AuthUser user) throws JsonProcessingException {
    // get basic user dto from authenticated User
    ModelMapper modelMapper = new ModelMapper();
    BasicUserDto basicUserDto = modelMapper.map(user, BasicUserDto.class);
    ObjectMapper mapper = new ObjectMapper();
    // User Object to JSON string
    String userJsonString = mapper.writeValueAsString(basicUserDto);
    return Jwts.builder()
        .setSubject(userJsonString)
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
        .signWith(SignatureAlgorithm.HS512, SECRET)
        .compact();
  }

  public static Authentication getAuthentication(HttpServletRequest request) throws IOException {
    String token = request.getHeader(CssConstant.HEADER_STRING);
    if (token != null) {
      // parse the token.
      Claims claims =
          Jwts.parser()
              .setSigningKey(SECRET)
              .parseClaimsJws(token.replace(CssConstant.TOKEN_PREFIX, ""))
              .getBody();
      LOGGER.debug("ID: {}", claims.getId());
      LOGGER.debug("EduSubject: {}", claims.getSubject());
      LOGGER.debug("Issuer: {}", claims.getIssuer());
      LOGGER.debug("Expiration: {}", claims.getExpiration());

      // check for expiry
      if (new Date().getTime() > claims.getExpiration().getTime()) {
        throw new UserExpiredRuntimeException("User Expired");
      }

      // TODO don't we want to check for accountlocked / act expired
      ObjectMapper mapper = new ObjectMapper();
      User user = mapper.readValue(claims.getSubject(), User.class);
      if (user != null) {
        AuthUser authUser = new AuthUser(user);
        return new CustomUserAuthentication(new CustomUserDetails(authUser));
      }
    }
    return null;
  }
}
