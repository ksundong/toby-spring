package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public User getUser(String id) {
    return userDao.get(id);
  }

  public void addUser(User user) {
    userDao.add(user);
    System.out.println(user.getId() + " 등록 성공");
  }

  public void deleteUsers() {
    userDao.deleteAll();
  }

  public int countUsers() {
    return userDao.getCount();
  }

  public List<User> getAll() {
    return userDao.getAll();
  }

  public void updateUser(User user) {
    userDao.update(user);
  }
}
