package dev.idion.springbook.learningtest.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CalcSumTest {

  Calculator calculator;
  String numFilepath;

  @BeforeEach
  void setUp() {
    this.calculator = new Calculator();
    this.numFilepath = getClass().getResource("/numbers.txt").getPath();
  }

  @Test
  void sumOfNumbers() throws IOException {
    assertThat((int) calculator.calcSum(this.numFilepath)).isEqualTo(10);
  }

  @Test
  void multiplyOfNumbers() throws IOException {
    assertThat((int) calculator.calcMultifly(this.numFilepath)).isEqualTo(24);
  }

}
