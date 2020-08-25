package dev.idion.springbook.user.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Aspect
@Component
public class TransactionAspect {

  private final PlatformTransactionManager transactionManager;

  public TransactionAspect(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @Around("execution(* *..*ServiceImpl.upgrade*(..))")
  public Object enableTransaction(ProceedingJoinPoint pjp) throws Throwable {
    TransactionStatus status =
        this.transactionManager.getTransaction(new DefaultTransactionDefinition());
    try {
      Object ret = pjp.proceed();
      this.transactionManager.commit(status);
      return ret;
    } catch (RuntimeException e) { // 예외 포장 하지않고 그대로 전달함
      this.transactionManager.rollback(status);
      throw e;
    }
  }
}
