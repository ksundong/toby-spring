package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class UserServiceImpl implements UserService {

  private final UserDao userDao;
  private final MailSender mailSender;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;

  public UserServiceImpl(UserDao userDao, MailSender mailSender,
      UserLevelUpgradePolicy userLevelUpgradePolicy) {
    this.userDao = userDao;
    this.mailSender = mailSender;
    this.userLevelUpgradePolicy = userLevelUpgradePolicy;
  }

  @Override
  public void upgradeLevels() {
    List<User> users = userDao.getAll();
    for (User user : users) {
      if (canUpgradeLevel(user)) {
        upgradeLevel(user);
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

  @Override
  public User get(String id) {
    return userDao.get(id);
  }

  @Override
  public List<User> getAll() {
    return userDao.getAll();
  }

  @Override
  public void deleteAll() {
    userDao.deleteAll();
  }

  @Override
  public void update(User user) {
    userDao.update(user);
  }

  private boolean canUpgradeLevel(User user) {
    return this.userLevelUpgradePolicy.canUpgradeLevel(user);
  }

  protected void upgradeLevel(User user) {
    this.userLevelUpgradePolicy.upgradeLevel(user);
    this.userDao.update(user);
    writeUpgradeEMail(user);
  }

  private void writeUpgradeEMail(User user) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setFrom("useradmin@ksug.org");
    mailMessage.setTo(user.getEmail());
    mailMessage.setSubject("Upgrade 안내");
    mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.");

    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronizationAdapter() {
          @Override
          public void afterCommit() {
            super.afterCommit();
            mailSender.send(mailMessage);
          }
        });
  }
}
