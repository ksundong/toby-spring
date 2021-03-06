package dev.idion.springbook.user.service;

import static dev.idion.springbook.user.service.UserLevelUpgradeNormalPolicy.MIN_LOGCOUNT_FOR_SILVER;
import static dev.idion.springbook.user.service.UserLevelUpgradeNormalPolicy.MIN_RECOMMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.idion.springbook.user.dao.TestDaoFactory;
import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import dev.idion.springbook.user.exception.TestUserServiceException;
import java.lang.reflect.Proxy;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDaoFactory.class)
class UserServiceTest {

  @Autowired
  UserDao userDao;

  @Autowired
  MailSender mailSender;

  @Autowired
  PlatformTransactionManager txManager;

  @Qualifier("userServiceImpl")
  @Autowired
  UserService userService;

  @Qualifier("testUserService")
  @Autowired
  UserService txUserService;

  @Qualifier("mockUserService")
  @Autowired
  UserService mockUserService;

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
    UserDao mockUserDao = (UserDao) ReflectionTestUtils.getField(this.mockUserService, "userDao");
    when(mockUserDao.getAll()).thenReturn(this.users);
    MailSender mockMailSender = (MailSender) ReflectionTestUtils
        .getField(this.mockUserService, "mailSender");

    this.mockUserService.upgradeLevels();

    verify(mockUserDao, times(2)).update(any(User.class));
    verify(mockUserDao, times(1)).update(users.get(1));
    verify(mockUserDao, times(1)).update(users.get(3));
    verify(mockUserDao, times(0)).update(users.get(2));
    verify(mockUserDao).update(users.get(1));
    assertThat(users.get(1).getLevel()).isEqualByComparingTo(Level.SILVER);
    verify(mockUserDao).update(users.get(3));
    assertThat(users.get(3).getLevel()).isEqualByComparingTo(Level.GOLD);

    ArgumentCaptor<SimpleMailMessage> mailMessageArgumentCaptor =
        ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mockMailSender, times(2)).send(mailMessageArgumentCaptor.capture());
    List<SimpleMailMessage> mailMessages = mailMessageArgumentCaptor.getAllValues();
    assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
    assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());
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
    userDao.deleteAll();
    for (User user : users) {
      userDao.add(user);
    }

    try {
      txUserService.upgradeLevels();
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

  @Test
  void advisorAutoProxyCreator() {
    assertThat(txUserService instanceof Proxy).isTrue();
    assertThat(userDao instanceof Proxy).isFalse();
  }

  @Test
  @Transactional(readOnly = true)
  void 읽기전용_트랜잭션_쓰기작업_수행시_오류_발생_테스트() {
    assertThatThrownBy(() -> {
      for (User user : this.users) {
        userService.add(user);
      }
    }).isInstanceOf(TransientDataAccessResourceException.class);
  }
}
