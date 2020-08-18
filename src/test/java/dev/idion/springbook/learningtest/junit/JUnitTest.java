package dev.idion.springbook.learningtest.junit;

import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

class JUnitTest {

  static JUnitTest testObject;

  @Test
  void test1() {
    assertNotSame(this, testObject);
    testObject = this;
  }

  @Test
  void test2() {
    assertNotSame(this, testObject);
    testObject = this;
  }

  @Test
  void test3() {
    assertNotSame(this, testObject);
    testObject = this;
  }
}
