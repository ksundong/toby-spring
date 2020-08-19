package dev.idion.springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

  public Integer calcSum(String filepath) throws IOException {
    return fileReadTemplate(filepath, br -> {
      int sum = 0;
      String line;
      while ((line = br.readLine()) != null) {
        sum += Integer.parseInt(line);
      }
      return sum;
    });
  }

  public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback)
      throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
      return callback.doSomethingWithReader(br);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw e;
    }
  }
}
