package com.cynosure.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CorsFilter implements Filter {

  private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);
  private static final String[] HEADERS_TO_TRY = {
    "origin",
    "host",
    "X-Forwarded-For",
    "Proxy-Client-IP",
    "WL-Proxy-Client-IP",
    "HTTP_X_FORWARDED_FOR",
    "HTTP_X_FORWARDED",
    "HTTP_X_CLUSTER_CLIENT_IP",
    "HTTP_CLIENT_IP",
    "HTTP_FORWARDED_FOR",
    "HTTP_FORWARDED",
    "HTTP_VIA",
    "REMOTE_ADDR"
  };

  @Value("${whitelist.restclient.ips}")
  String[] allowDomain;

  public static String getClientIpAddress(HttpServletRequest request) {
    for (String header : HEADERS_TO_TRY) {
      String ip = request.getHeader(header);
      if (ip != null
          && ip.length() != 0
          && !"unknown".equalsIgnoreCase(ip)
          && !ip.contains("chrome-extension")) {
        return ip;
      }
    }
    return request.getRemoteAddr();
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    Set<String> allowedOrigins = new HashSet<>(Arrays.asList(allowDomain));
    HttpServletRequest request = (HttpServletRequest) req;
    String origin = getClientIpAddress(request);
    if (allowedOrigins.contains(origin)) {
      LOGGER.debug("Requested origin is allowed {} ", origin);
      HttpServletResponse response = (HttpServletResponse) res;
      response.setHeader("Access-Control-Allow-Origin", origin);
      response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setHeader("Access-Control-Allow-Credentials", "true");
      response.setHeader(
          "Access-Control-Allow-Headers",
          "Origin, X-Requested-With, Content-Type, Accept, Authorization");
      response.setHeader("Access-Control-Expose-Headers", "Authorization");
      chain.doFilter(req, res);
    } else {
      LOGGER.error("Requested origin is NOT allowed {}", origin);
    }
  }
}
