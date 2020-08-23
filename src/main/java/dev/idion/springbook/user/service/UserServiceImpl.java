package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserDao userDao;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;

  public UserServiceImpl(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy) {
    this.userDao = userDao;
    this.userLevelUpgradePolicy = userLevelUpgradePolicy;
  }

  @Override
  public void upgradeLevels() {
    throw new RuntimeException("Unimplemented");
  }

  @Override
  public void upgradeLevels(List<SimpleMailMessage> mailMessages) {
    List<User> users = userDao.getAll();
    for (User user : users) {
      if (canUpgradeLevel(user)) {
        upgradeLevel(user, mailMessages);
      }
    }
  }

  @Override
  public void add(User user) {
    if (user.getLevel() == null) {
      user.setLevel(Level.BASIC);
    }
    this.userDao.add(user);
  }

  private boolean canUpgradeLevel(User user) {
    return this.userLevelUpgradePolicy.canUpgradeLevel(user);
  }

  protected void upgradeLevel(User user, List<SimpleMailMessage> mailMessages) {
    this.userLevelUpgradePolicy.upgradeLevel(user);
    this.userDao.update(user);
    writeUpgradeEMail(user, mailMessages);
  }

  private void writeUpgradeEMail(User user, List<SimpleMailMessage> mailMessages) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setFrom("useradmin@ksug.org");
    mailMessage.setTo(user.getEmail());
    mailMessage.setSubject("Upgrade 안내");
    mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");

    mailMessages.add(mailMessage);
  }
}
