package dev.idion.springbook.learningtest.spring.factorybean;

public class Message {

  private String text;

  private Message(String text) {
    this.text = text;
  }

  public static Message newMessage(String text) {
    return new Message(text);
  }

  public String getText() {
    return text;
  }
}
