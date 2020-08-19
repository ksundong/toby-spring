package dev.idion.springbook.learningtest.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class CalcSumTest {

  @Test
  void sumOfNumbers() throws IOException {
    Calculator calculator = new Calculator();
    int sum = calculator.calcSum(getClass().getResource("/numbers.txt").getPath());
    assertThat(sum).isEqualTo(10);
  }
}
