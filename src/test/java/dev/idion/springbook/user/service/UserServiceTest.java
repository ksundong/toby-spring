package dev.idion.springbook.user.service;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDaoFactory.class)
class UserServiceTest {

  @Autowired
  UserDao userDao;

  @Autowired
  UserService userService;

  List<User> users;

  @BeforeEach
  void setUp() {
    users = Lists.newArrayList(
        new User("bumjin", "박범진", "p1", 49, 0, Level.BASIC),
        new User("joytouch", "강명성", "p2", 50, 0, Level.BASIC),
        new User("erwins", "신승한", "p3", 60, 29, Level.SILVER),
        new User("madnite1", "이상호", "p4", 60, 30, Level.SILVER),
        new User("green", "오민규", "p5", 100, 100, Level.GOLD)
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
    checkLevel(users.get(0), Level.BASIC);
    checkLevel(users.get(1), Level.SILVER);
    checkLevel(users.get(2), Level.SILVER);
    checkLevel(users.get(3), Level.GOLD);
    checkLevel(users.get(4), Level.GOLD);
  }

  private void checkLevel(User user, Level expectedLevel) {
    User userUpdate = userDao.get(user.getId());
    assertThat(userUpdate.getLevel()).isEqualByComparingTo(expectedLevel);
  }
}
