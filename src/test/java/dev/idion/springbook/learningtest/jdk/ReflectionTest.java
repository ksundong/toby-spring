package dev.idion.springbook.learningtest.jdk;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class ReflectionTest {

  @Test
  void invokeMethod()
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    String name = "spring";

    // length()
    assertThat(name.length()).isEqualTo(6);

    Method lengthMethod = String.class.getMethod("length");
    assertThat(lengthMethod.invoke(name)).isEqualTo(6);

    // charAt()
    assertThat(name.charAt(0)).isEqualTo('s');

    Method charAtMethod = String.class.getMethod("charAt", int.class);
    assertThat(charAtMethod.invoke(name, 0)).isEqualTo('s');
  }
}
