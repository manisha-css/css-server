package com.cynosure.security.filter;

import com.cynosure.dto.AuthUser;
import com.cynosure.dto.BasicUserDto;
import com.cynosure.dto.InfoResponse;
import com.cynosure.dto.UserDto;
import com.cynosure.exception.CssAppRuntimeException;
import com.cynosure.security.model.CustomUserDetails;
import com.cynosure.security.service.TokenAuthenticationService;
import com.cynosure.util.CssConstant;
import com.cynosure.util.Translator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

  final UserDetailsService customUserDetailsService;

  @Autowired private MessageSource messageSource;

  @Autowired private Translator translator;

  public JWTLoginFilter(
      String url, AuthenticationManager authManager, UserDetailsService customUserDetailsService) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(authManager);
    this.customUserDetailsService = customUserDetailsService;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws IOException, ServletException {
    // UserDto can not be used as it does not have password
    UserDto userDto = new ObjectMapper().readValue(req.getInputStream(), UserDto.class);
    return getAuthenticationManager()
        .authenticate(
            new UsernamePasswordAuthenticationToken(
                userDto.getUserName(), userDto.getUserPassword(), Collections.emptyList()));
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest req, HttpServletResponse response, FilterChain chain, Authentication auth)
      throws IOException, ServletException {
    CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
    AuthUser authenticatedUser = customUserDetails.getAuthUser();
    String jwtToken = TokenAuthenticationService.addAuthentication(authenticatedUser);
    response.addHeader(CssConstant.HEADER_STRING, CssConstant.TOKEN_PREFIX + " " + jwtToken);
    // Add the authentication to the Security context
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json");
    // get basic user dto from authenticated User - currently picked up from authenticatedUser but
    // else UI can send separate request to get the details using ID
    ModelMapper modelMapper = new ModelMapper();
    BasicUserDto basicUserDto = modelMapper.map(authenticatedUser, BasicUserDto.class);

    // translator (which eventually uses messagesource)  may not be initialized at this point so
    // need to explicitly set
    if (messageSource == null) {
      ServletContext servletContext = req.getServletContext();
      WebApplicationContext webApplicationContext =
          WebApplicationContextUtils.getWebApplicationContext(servletContext);
      messageSource = webApplicationContext.getBean(MessageSource.class);
    }
    if (translator == null) {
      ServletContext servletContext = req.getServletContext();
      WebApplicationContext webApplicationContext =
          WebApplicationContextUtils.getWebApplicationContext(servletContext);
      translator = webApplicationContext.getBean(Translator.class);
    }
    // get json string from basicUserDto
    ObjectMapper mapper = new ObjectMapper();
    InfoResponse<BasicUserDto> infoResponse = new InfoResponse();
    infoResponse.setMessage(translator.toLocale("login.success", null));
    infoResponse.setResult(basicUserDto);
    try {
      response.getWriter().write(mapper.writeValueAsString(infoResponse));
    } catch (IOException e) {
      throw new CssAppRuntimeException("IOException in populateWithJSON", e);
    }
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {
    /*
    InternalAuthenticationServiceException is thrown with different message / cause
    There are total 4 exception causes related to 4 security flags
    Order of exception is as follows
    Locked - Disabled - account expired - credentials expired
    Also BadCredentialsException & application specific  UsernameNotFoundException are thrown here
    Custom message for these exception - ExceptionHandler filter will handle it
    Locked => for verification
    Disabled => Block user by admin => admin part not covered
    */
    if (exception instanceof InternalAuthenticationServiceException) {
      if (exception.getCause() instanceof LockedException) {
        throw new LockedException(exception.getMessage());
      }
      if (exception.getCause() instanceof DisabledException) {
        throw new DisabledException(exception.getMessage());
      } else {
        throw new CssAppRuntimeException(exception.getMessage());
      }
    }
    throw exception;
  }
}
