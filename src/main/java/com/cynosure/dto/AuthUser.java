package com.cynosure.dto;

import com.cynosure.model.Role;
import com.cynosure.model.User;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// kept this separate as eventually User entity fields & AuthUser may differ significantly
public class AuthUser implements Serializable {
  private Long id;
  private String givenName;
  private String userName;
  private String password;
  private Set<Role> roles = new HashSet<>();
  private boolean accountExpired;
  private boolean accountLocked;
  private boolean credentialsExpired;
  private boolean accountDisabled;

  public AuthUser(User user) {
    this.id = user.getId();
    this.givenName = user.getGivenName();
    this.userName = user.getUserName();
    this.password = user.getPassword();
    this.roles = user.getRoles();
    this.accountExpired = user.isAccountExpired();
    this.accountLocked = user.isAccountLocked();
    this.credentialsExpired = user.isCredentialsExpired();
    this.accountDisabled = user.isAccountDisabled();
  }
}
