package dev.idion.springbook.user.service;

import java.lang.reflect.Proxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object> {

  private final Object target;
  private final PlatformTransactionManager transactionManager;
  private final String pattern;
  private final Class<?> serviceInterface;

  public TxProxyFactoryBean(Object target, PlatformTransactionManager transactionManager,
      String pattern, Class<?> serviceInterface) {
    this.target = target;
    this.transactionManager = transactionManager;
    this.pattern = pattern;
    this.serviceInterface = serviceInterface;
  }

  @Override
  public Object getObject() throws Exception {
    TransactionHandler txHandler = new TransactionHandler(target, transactionManager, pattern);
    return Proxy
        .newProxyInstance(getClass().getClassLoader(), new Class[]{serviceInterface}, txHandler);
  }

  @Override
  public Class<?> getObjectType() {
    return serviceInterface;
  }

  @Override
  public boolean isSingleton() {
    return false;
  }
}
