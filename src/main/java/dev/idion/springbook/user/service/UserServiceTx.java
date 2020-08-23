package dev.idion.springbook.user.service;

import dev.idion.springbook.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@Primary
public class UserServiceTx implements UserService {

  @Qualifier("userServiceImpl")
  private final UserService userService;

  private final PlatformTransactionManager txManager;
  private final MailSender mailSender;

  public UserServiceTx(UserService userService, PlatformTransactionManager txManager,
      MailSender mailSender) {
    this.userService = userService;
    this.txManager = txManager;
    this.mailSender = mailSender;
  }

  @Override
  public void upgradeLevels() {
    List<SimpleMailMessage> mailMessages = new ArrayList<>();
    this.upgradeLevels(mailMessages);
  }

  @Override
  public void upgradeLevels(List<SimpleMailMessage> mailMessages) {
    TransactionStatus status = this.txManager.getTransaction(new DefaultTransactionDefinition());
    try {
      this.userService.upgradeLevels(mailMessages);
      this.txManager.commit(status);
      this.mailSender.send(mailMessages.toArray(new SimpleMailMessage[0]));
    } catch (RuntimeException e) {
      this.txManager.rollback(status);
      throw e;
    }
  }

  @Override
  public void add(User user) {
    this.userService.add(user);
  }
}
