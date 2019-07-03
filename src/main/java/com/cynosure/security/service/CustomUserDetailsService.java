package com.cynosure.security.service;

import com.cynosure.dto.AuthUser;
import com.cynosure.model.User;
import com.cynosure.repo.UserRepo;
import com.cynosure.security.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
    implements org.springframework.security.core.userdetails.UserDetailsService {

  private final AccountStatusUserDetailsChecker detailsChecker =
      new AccountStatusUserDetailsChecker();
  @Autowired private UserRepo userRepo;

  @Override
  public UserDetails loadUserByUsername(String userName) {
    User user = userRepo.findByUserName(userName).orElse(null);
    if (user == null) {
      throw new UsernameNotFoundException(userName);
    }
    UserDetails userDetails = new CustomUserDetails(new AuthUser(user));
    detailsChecker.check(userDetails);
    return userDetails;
  }
}
