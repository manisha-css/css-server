package com.cynosure.security.model;

import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class CustomUserAuthentication implements Authentication {

  private final CustomUserDetails customUserDetails;
  private boolean authenticated = true;

  public CustomUserAuthentication(CustomUserDetails customUserDetails) {
    this.customUserDetails = customUserDetails;
  }

  @Override
  public String getName() {
    return customUserDetails.getUsername();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return customUserDetails.getAuthorities();
  }

  @Override
  public Object getCredentials() {
    return customUserDetails.getPassword();
  }

  @Override
  public CustomUserDetails getDetails() {
    return customUserDetails;
  }

  @Override
  public Object getPrincipal() {
    return customUserDetails.getUsername();
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void setAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;
  }
}
