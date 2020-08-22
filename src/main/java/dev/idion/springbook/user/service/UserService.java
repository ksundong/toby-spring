package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class UserService {

  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOMMEND_FOR_GOLD = 30;

  private final UserDao userDao;
  private final MailSender mailSender;
  private final PlatformTransactionManager txManager;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;

  public UserService(UserDao userDao, MailSender mailSender,
      PlatformTransactionManager txManager,
      UserLevelUpgradePolicy userLevelUpgradePolicy) {
    this.userDao = userDao;
    this.mailSender = mailSender;
    this.txManager = txManager;
    this.userLevelUpgradePolicy = userLevelUpgradePolicy;
  }

  public void upgradeLevels() {
    TransactionStatus status = this.txManager.getTransaction(new DefaultTransactionDefinition());
    List<SimpleMailMessage> mailMessages = new ArrayList<>();
    try {
      List<User> users = userDao.getAll();
      for (User user : users) {
        if (canUpgradeLevel(user)) {
          upgradeLevel(user, mailMessages);
        }
      }
      this.txManager.commit(status);
      this.mailSender.send(mailMessages.toArray(new SimpleMailMessage[0]));
    } catch (RuntimeException e) {
      this.txManager.rollback(status);
      throw e;
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
