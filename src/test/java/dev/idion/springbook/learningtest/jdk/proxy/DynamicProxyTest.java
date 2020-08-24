package dev.idion.springbook.learningtest.jdk.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

class DynamicProxyTest {

  @Test
  void targetTest() {
    Hello hello = new HelloTarget();
    assertThat(hello.sayHello("Toby")).isEqualTo("Hello Toby");
    assertThat(hello.sayHi("Toby")).isEqualTo("Hi Toby");
    assertThat(hello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
  }

  @Test
  void dynamicProxyTest() {
    Hello proxiedHello = (Hello) Proxy.newProxyInstance(
        getClass().getClassLoader(),
        new Class[]{Hello.class},
        new UppercaseHandler(new HelloTarget())
    );
    assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
    assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
    assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
  }

  @Test
  void proxyFactoryBean() {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(new HelloTarget());
    pfBean.addAdvice(new UppercaseAdvice()); // 여러개 추가 가능(부가기능을 담음)

    Hello proxiedHello = (Hello) pfBean.getObject();
    assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
    assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
    assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
  }

  @Test
  void pointcutAdvisor() {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(new HelloTarget());

    NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
    pointcut.setMappedName("sayH*");

    pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

    Hello proxiedHello = (Hello) pfBean.getObject();
    assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
    assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
    assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
  }

  @Test
  void classNamePointcutAdvisor() {
    // 포인트컷 준비
    NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
      @Override
      public ClassFilter getClassFilter() {
        return clazz -> clazz.getSimpleName().startsWith("HelloT");
      }
    };
    classMethodPointcut.setMappedName("sayH*");

    // 테스트
    checkAdviced(new HelloTarget(), classMethodPointcut, true);

    class HelloWorld extends HelloTarget {}
    checkAdviced(new HelloWorld(), classMethodPointcut, false);

    class HelloToby extends HelloTarget {}
    checkAdviced(new HelloToby(), classMethodPointcut, true);
  }

  private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
    ProxyFactoryBean pfBean = new ProxyFactoryBean();
    pfBean.setTarget(target);
    pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
    Hello proxiedHello = (Hello) pfBean.getObject();

    if (adviced) {
      assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
      assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
      assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
    } else {
      assertThat(proxiedHello.sayHello("Toby")).isEqualTo("Hello Toby");
      assertThat(proxiedHello.sayHi("Toby")).isEqualTo("Hi Toby");
      assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
    }
  }
}
