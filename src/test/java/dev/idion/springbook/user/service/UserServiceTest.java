package dev.idion.springbook.user.service;

import static dev.idion.springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static dev.idion.springbook.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import dev.idion.springbook.user.dao.TestDaoFactory;
import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDaoFactory.class)
class UserServiceTest {

  @Autowired
  UserDao userDao;

  @Autowired
  MailSender mailSender;

  @Autowired
  PlatformTransactionManager txManager;

  @Autowired
  UserService userService;

  @Autowired
  UserLevelUpgradePolicy userLevelUpgradePolicy;

  List<User> users;

  @BeforeEach
  void setUp() {
    users = Lists.newArrayList(
        new User("bumjin", "박범진", "p1", MIN_LOGCOUNT_FOR_SILVER - 1, 0, Level.BASIC,
            "bumjin@springbook.test"),
        new User("joytouch", "강명성", "p2", MIN_LOGCOUNT_FOR_SILVER, 0, Level.BASIC,
            "joytouch@springbook.test"),
        new User("erwins", "신승한", "p3", 60, MIN_RECOMMEND_FOR_GOLD - 1, Level.SILVER,
            "erwins@springbook.test"),
        new User("madnite1", "이상호", "p4", 60, MIN_RECOMMEND_FOR_GOLD, Level.SILVER,
            "madnite1@springbook.test"),
        new User("green", "오민규", "p5", 100, Integer.MAX_VALUE, Level.GOLD, "green@springbook.test")
    );
  }

  @Test
  void bean() {
    assertThat(userService).isNotNull();
  }

  @Test
  void upgradeLevels() {
    userDao.deleteAll();
    for (User user : users) {
      userDao.add(user);
    }

    userService.upgradeLevels();
    checkLevelUpgraded(users.get(0), false);
    checkLevelUpgraded(users.get(1), true);
    checkLevelUpgraded(users.get(2), false);
    checkLevelUpgraded(users.get(3), true);
    checkLevelUpgraded(users.get(4), false);
  }

  @Test
  void add() {
    userDao.deleteAll();

    User userWithLevel = users.get(4);
    User userWithoutLevel = users.get(0);
    userWithoutLevel.setLevel(null);

    userService.add(userWithLevel);
    userService.add(userWithoutLevel);

    User userWithLevelRead = userDao.get(userWithLevel.getId());
    User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    assertThat(userWithLevelRead).isEqualToComparingFieldByField(userWithLevel);
    assertThat(userWithoutLevelRead).isEqualToComparingFieldByField(userWithoutLevel);
  }

  @Test
  void upgradeAllOrNothing() {
    UserService testUserService =
        new TestUserService(this.userDao, this.mailSender, this.txManager,
            this.userLevelUpgradePolicy,
            users.get(3).getId());

    userDao.deleteAll();
    for (User user : users) {
      userDao.add(user);
    }

    try {
      testUserService.upgradeLevels();
      fail("TestUserServiceException expected.");
    } catch (TestUserServiceException e) {
    }

    User user1 = users.get(1);
    checkLevelUpgraded(user1, false);
    checkLevelUpgraded(userDao.get(user1.getId()), false);
  }

  private void checkLevelUpgraded(User user, boolean upgraded) {
    User userUpdate = userDao.get(user.getId());
    if (upgraded) {
      assertThat(userUpdate.getLevel()).isEqualByComparingTo(user.getLevel().nextLevel());
    } else {
      assertThat(userUpdate.getLevel()).isEqualByComparingTo(user.getLevel());
    }
  }

  static class TestUserService extends UserService {

    private final String id;

    public TestUserService(UserDao userDao, MailSender mailSender,
        PlatformTransactionManager txManager, UserLevelUpgradePolicy userLevelUpgradePolicy,
        String id) {
      super(userDao, mailSender, txManager, userLevelUpgradePolicy);
      this.id = id;
    }

    @Override
    protected void upgradeLevel(User user) {
      if (user.getId().equals(this.id)) {
        throw new TestUserServiceException();
      }
      super.upgradeLevel(user);
    }
  }

  static class TestUserServiceException extends RuntimeException {

  }
}
