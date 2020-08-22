package dev.idion.springbook.user.service;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import java.util.List;
import java.util.Properties;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
public class UserService {

  public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
  public static final int MIN_RECOMMEND_FOR_GOLD = 30;

  private final UserDao userDao;
  private final PlatformTransactionManager txManager;
  private final UserLevelUpgradePolicy userLevelUpgradePolicy;

  public UserService(UserDao userDao, PlatformTransactionManager txManager,
      UserLevelUpgradePolicy userLevelUpgradePolicy) {
    this.userDao = userDao;
    this.txManager = txManager;
    this.userLevelUpgradePolicy = userLevelUpgradePolicy;
  }

  public void upgradeLevels() {
    TransactionStatus status = this.txManager.getTransaction(new DefaultTransactionDefinition());
    try {
      List<User> users = userDao.getAll();
      for (User user : users) {
        if (canUpgradeLevel(user)) {
          upgradeLevel(user);
        }
      }
      this.txManager.commit(status);
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

  protected void upgradeLevel(User user) {
    this.userLevelUpgradePolicy.upgradeLevel(user);
    this.userDao.update(user);
    sendUpgradeEMail(user);
  }

  private void sendUpgradeEMail(User user) {
    Properties props = new Properties();
    props.put("mail.smtp.host", "mail.ksug.org");
    Session s = Session.getInstance(props, null);

    MimeMessage message = new MimeMessage(s);
    try {
      message.setFrom(new InternetAddress("useradmin@ksug.org"));
      message.addRecipient(RecipientType.TO, new InternetAddress(user.getEmail()));
      message.setSubject("Upgrade 안내", "UTF-8");
      message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다.",
          "UTF-8", "html");

      Transport.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}
