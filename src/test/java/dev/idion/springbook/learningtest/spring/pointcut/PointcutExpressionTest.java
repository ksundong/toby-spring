package dev.idion.springbook.learningtest.spring.pointcut;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

class PointcutExpressionTest {

  @Test
  void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression("execution(public int "
        + "dev.idion.springbook.learningtest.spring.pointcut.Target.minus(int, int) "
        + "throws java.lang.RuntimeException)");

    // Target.minus()
    assertThat(pointcut.getClassFilter().matches(Target.class) &&
        pointcut.getMethodMatcher().matches(
            Target.class.getMethod("minus", int.class, int.class), null)).isTrue();

    // Target.plus()
    assertThat(pointcut.getClassFilter().matches(Target.class) &&
        pointcut.getMethodMatcher().matches(
            Target.class.getMethod("plus", int.class, int.class), null)).isFalse();

    // Bean.method()
    assertThat(pointcut.getClassFilter().matches(Bean.class) &&
        pointcut.getMethodMatcher().matches(
            Bean.class.getMethod("method"), null)).isFalse();
  }
}
