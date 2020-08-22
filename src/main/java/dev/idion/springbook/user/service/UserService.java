package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class UserService {

  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOMMEND_FOR_GOLD = 30;

  private final UserDao userDao;
  private final DataSource dataSource;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;

  public UserService(UserDao userDao, DataSource dataSource,
      UserLevelUpgradePolicy userLevelUpgradePolicy) {
    this.userDao = userDao;
    this.dataSource = dataSource;
    this.userLevelUpgradePolicy = userLevelUpgradePolicy;
  }

  public void upgradeLevels() {
    TransactionSynchronizationManager.initSynchronization();
    try (Connection c = DataSourceUtils.getConnection(this.dataSource)) {
      c.setAutoCommit(false);

      try {
        List<User> users = userDao.getAll();
        for (User user : users) {
          if (canUpgradeLevel(user)) {
            upgradeLevel(user);
          }
        }
        c.commit();
      } catch (DuplicateKeyException e) {
        e.printStackTrace();
        c.rollback();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      TransactionSynchronizationManager.unbindResource(this.dataSource);
      TransactionSynchronizationManager.clearSynchronization();
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

  protected void upgradeLevel(User user) {
    this.userLevelUpgradePolicy.upgradeLevel(user);
    this.userDao.update(user);
  }
}
