package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.User;
import dev.idion.springbook.user.exception.TestUserServiceException;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;

@Service
class TestUserService extends UserServiceImpl {

  private final String id = "madnite1";

  public TestUserService(UserDao userDao, MailSender mailSender,
      UserLevelUpgradePolicy userLevelUpgradePolicy) {
    super(userDao, mailSender, userLevelUpgradePolicy);
  }

  @Override
  protected void upgradeLevel(User user) {
    if (user.getId().equals(this.id)) {
      throw new TestUserServiceException();
    }
    super.upgradeLevel(user);
  }
}
