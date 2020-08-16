package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.User;
import java.sql.SQLException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public User getUser(String id) throws SQLException {
    return userDao.get(id);
  }

  public void addUser(String id, String name, String password) throws SQLException {
    User user = new User();
    user.setId(id);
    user.setName(name);
    user.setPassword(password);

    userDao.add(user);
    System.out.println(user.getId() + " 등록 성공");
  }
}
