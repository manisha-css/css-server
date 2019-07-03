package com.cynosure.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "username", unique = true, nullable = false)
  private String userName; // userName is same as email Id

  @Column(name = "givenname", nullable = false)
  private String givenName;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String verificationCode;

  private String publicProfile;

  @Enumerated(EnumType.STRING)
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "role")
  private Set<Role> roles = new HashSet<>();

  private boolean accountExpired;

  private boolean
      accountLocked; // used during verification of account - set to true if not verified

  private boolean credentialsExpired;

  private boolean accountDisabled;
}
