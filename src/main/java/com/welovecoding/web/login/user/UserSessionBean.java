package com.welovecoding.web.login.user;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named
@SessionScoped
public class UserSessionBean implements Serializable {

  private static final long serialVersionUID = 1L;
  private User user;
  private boolean isLoggedIn;

  @PostConstruct
  void init() {
    isLoggedIn = false;
    user = new User();
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isIsLoggedIn() {
    return isLoggedIn;
  }

  public void setIsLoggedIn(boolean isLoggedIn) {
    this.isLoggedIn = isLoggedIn;
  }

}
