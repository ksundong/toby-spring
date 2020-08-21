package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserDao userDao;

  public UserService(UserDao userDao) {
    this.userDao = userDao;
  }

  public void upgradeLevels() {
    List<User> users = userDao.getAll();
    for (User user : users) {
      boolean levelHasChanged;
      if (user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
        user.setLevel(Level.SILVER);
        levelHasChanged = true;
      } else if (user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
        user.setLevel(Level.GOLD);
        levelHasChanged = true;
      } else {
        levelHasChanged = false;
      }

      if (levelHasChanged) {
        userDao.update(user);
      }
    }
  }
}
