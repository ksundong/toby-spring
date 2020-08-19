package dev.idion.springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

  public Integer calcSum(String filepath) throws IOException {
    return lineReadTemplate(filepath, 0, (line, value) -> value + Integer.parseInt(line));
  }

  public Integer calcMultiply(String filepath) throws IOException {
    return lineReadTemplate(filepath, 1, (line, value) -> value * Integer.parseInt(line));
  }

  public Integer lineReadTemplate(String filepath, int initVal, LineCallback callback)
      throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
      int res = initVal;
      String line;
      while ((line = br.readLine()) != null) {
        res = callback.doSomethingWithLine(line, res);
      }
      return res;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw e;
    }
  }
}
