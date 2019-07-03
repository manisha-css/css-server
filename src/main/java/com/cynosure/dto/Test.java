package com.cynosure.dto;

import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Test implements Serializable {
  private Long id;
  private String userName;
  private String password;

  public Test(Long id, String userName, String password) {
    this.id = id;
    this.userName = userName;
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Test test = (Test) o;
    return id.equals(test.id) && userName.equals(test.userName) && password.equals(test.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userName, password);
  }
}
