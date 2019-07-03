package com.cynosure.security.model;

import com.cynosure.dto.AuthUser;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private AuthUser authUser;

  public CustomUserDetails(AuthUser authUser) {
    this.authUser = authUser;
  }

  @Override
  public String getUsername() {
    return authUser.getUserName();
  }

  @Override
  public String getPassword() {
    return authUser.getPassword();
  }

  @Override
  public boolean isAccountNonExpired() {
    return !authUser.isAccountExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !authUser.isAccountLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return !authUser.isCredentialsExpired();
  }

  @Override
  public boolean isEnabled() {
    return !authUser.isAccountDisabled();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authUser.getRoles();
  }

  public AuthUser getAuthUser() {
    return authUser;
  }

  public void setAuthUser(AuthUser authUser) {
    this.authUser = authUser;
  }
}
