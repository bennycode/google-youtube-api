package com.welovecoding.web.login.user;

import java.io.Serializable;

public class User implements Serializable {

  private static final long serialVersionUID = 1L;
  private String email;

  public User() {
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(
    String email) {
    this.email = email;
  }

}
