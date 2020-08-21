package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOMMEND_FOR_GOLD = 30;

  private final UserDao userDao;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;

  public UserService(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy) {
    this.userDao = userDao;
    this.userLevelUpgradePolicy = userLevelUpgradePolicy;
  }

  public void upgradeLevels() {
    List<User> users = userDao.getAll();
    for (User user : users) {
      if (canUpgradeLevel(user)) {
        upgradeLevel(user);
      }
    }
  }

  public void add(User user) {
    if (user.getLevel() == null) {
      user.setLevel(Level.BASIC);
    }
    this.userDao.add(user);
  }

  private boolean canUpgradeLevel(User user) {
    return this.userLevelUpgradePolicy.canUpgradeLevel(user);
  }

  private void upgradeLevel(User user) {
    this.userLevelUpgradePolicy.upgradeLevel(user);
    this.userDao.update(user);
  }
}
