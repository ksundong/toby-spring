package dev.idion.springbook.learningtest.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JUnitTest {

  static Set<JUnitTest> testObjects = new HashSet<>();

  @Test
  void test1() {
    assertThat(this).isNotIn(testObjects);
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
  }

  @Test
  void test2() {
    assertThat(this).isNotIn(testObjects);
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
  }

  @Test
  void test3() {
    assertThat(this).isNotIn(testObjects);
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
  }
}
