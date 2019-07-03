package com.cynosure.filter.exceptionhandler;

import com.cynosure.dto.ErrorResponse;
import com.cynosure.dto.InfoResponse;
import com.cynosure.exception.CssAppRuntimeException;
import com.cynosure.exception.InvalidJWTTokenSignatureRuntimeException;
import com.cynosure.exception.UserExpiredRuntimeException;
import com.cynosure.util.Translator;
import com.cynosure.util.UtilityService;
import io.jsonwebtoken.MalformedJwtException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Instant;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired private UtilityService utilityService;

  @Autowired private Translator translator;

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(req, res);
    } catch (CssAppRuntimeException ex) {
      LOGGER.error("Filter CssAppRuntimeException {}", ex);
      ErrorResponse errorResponse =
          new ErrorResponse(Instant.now(), ex.getMessage(), req.getRequestURI());
      res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      res.getWriter().write(utilityService.convertObjectToJson(errorResponse));
    } catch (MalformedJwtException ex) {
      LOGGER.error("Malformed JWT token, Exception: {}", ex);
      InfoResponse errorResponse = new InfoResponse();
      errorResponse.setMessage(translator.toLocale("login.token.invalid", null));
      res.setStatus(HttpStatus.UNAUTHORIZED.value());
      res.getWriter().write(utilityService.convertObjectToJson(errorResponse));
    } catch (InvalidJWTTokenSignatureRuntimeException ex) {
      LOGGER.error("Invalid JWT token, Exception: {}", ex);
      InfoResponse errorResponse = new InfoResponse();
      errorResponse.setMessage(translator.toLocale("login.token.invalid", null));
      res.setStatus(HttpStatus.FORBIDDEN.value());
      res.getWriter().write(utilityService.convertObjectToJson(errorResponse));
    } catch (UserExpiredRuntimeException ex) {
      LOGGER.error("Token expired {}", ex);
      InfoResponse errorResponse = new InfoResponse();
      errorResponse.setMessage(translator.toLocale("login.token.expired", null));
      res.setStatus(HttpStatus.FORBIDDEN.value());
      res.getWriter().write(utilityService.convertObjectToJson(errorResponse));
    } catch (BadCredentialsException ex) {
      // custom error response class used across the project
      LOGGER.error("BadCredentialsException {}", ex);
      InfoResponse errorResponse = new InfoResponse();
      errorResponse.setMessage(translator.toLocale("login.bad.credentials", null));
      res.setStatus(HttpStatus.UNAUTHORIZED.value());
      res.getWriter().write(utilityService.convertObjectToJson(errorResponse));
    } catch (LockedException ex) {
      // custom error response class used across the project
      LOGGER.error("LockedException {}", ex);
      InfoResponse errorResponse = new InfoResponse();
      errorResponse.setMessage(translator.toLocale("login.user.notverified", null));
      res.setStatus(HttpStatus.FORBIDDEN.value());
      res.getWriter().write(utilityService.convertObjectToJson(errorResponse));
    } catch (DisabledException ex) {
      // custom error response class used across the project
      LOGGER.error("DisabledException {}", ex);
      InfoResponse errorResponse = new InfoResponse();
      errorResponse.setMessage(translator.toLocale("login.user.blocked", null));
      res.setStatus(HttpStatus.FORBIDDEN.value());
      res.getWriter().write(utilityService.convertObjectToJson(errorResponse));
    } catch (Exception ex) {
      LOGGER.error("Filter Exception {}", ex);
      ErrorResponse errorResponse =
          new ErrorResponse(Instant.now(), ex.getMessage(), req.getRequestURI());
      res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      res.getWriter().write(utilityService.convertObjectToJson(errorResponse));
    }
  }
}
