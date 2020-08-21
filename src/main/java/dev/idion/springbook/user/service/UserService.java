package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }
}
