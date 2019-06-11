package com.cynosure.filter.exceptionhandler;

import com.cynosure.dto.ErrorResponse;
import com.cynosure.exception.CssAppRuntimeException;
import com.cynosure.util.UtilityService;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired UtilityService utilityService;

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
    } catch (Exception ex) {
      LOGGER.error("Filter Exception {}", ex);
      ErrorResponse errorResponse =
          new ErrorResponse(Instant.now(), ex.getMessage(), req.getRequestURI());
      res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      res.getWriter().write(utilityService.convertObjectToJson(errorResponse));
    }
  }
}
