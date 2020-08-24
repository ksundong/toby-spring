package dev.idion.springbook.learningtest.jdk.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class UppercaseAdvice implements MethodInterceptor {

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    String ret = (String) invocation.proceed(); // MethodInvocation은 타깃 오브젝트를 알고 있음.
    return ret.toUpperCase();
  }
}
