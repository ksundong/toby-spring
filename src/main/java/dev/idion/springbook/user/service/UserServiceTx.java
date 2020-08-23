package dev.idion.springbook.user.service;

import dev.idion.springbook.user.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
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

  public UserServiceTx(UserService userService, PlatformTransactionManager txManager) {
    this.userService = userService;
    this.txManager = txManager;
  }

  @Override
  public void upgradeLevels() {
    TransactionStatus status = this.txManager.getTransaction(new DefaultTransactionDefinition());
    try {
      this.userService.upgradeLevels();
      this.txManager.commit(status);
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
