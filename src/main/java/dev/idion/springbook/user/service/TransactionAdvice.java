package dev.idion.springbook.user.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {

  private final PlatformTransactionManager transactionManager;

  public TransactionAdvice(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    TransactionStatus status =
        this.transactionManager.getTransaction(new DefaultTransactionDefinition());
    try {
      Object ret = invocation.proceed();
      this.transactionManager.commit(status);
      return ret;
    } catch (RuntimeException e) { // 예외 포장 하지않고 그대로 전달함
      this.transactionManager.rollback(status);
      throw e;
    }
  }
}
