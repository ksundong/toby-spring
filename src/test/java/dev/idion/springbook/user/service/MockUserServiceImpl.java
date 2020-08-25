package dev.idion.springbook.user.service;

import static org.mockito.Mockito.mock;

import dev.idion.springbook.user.dao.UserDao;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;

@Service
public class MockUserServiceImpl extends UserServiceImpl {

  public MockUserServiceImpl(UserLevelUpgradePolicy userLevelUpgradePolicy) {
    super(mock(UserDao.class), mock(MailSender.class), userLevelUpgradePolicy);
  }
}
