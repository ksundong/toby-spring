package dev.idion.springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class DummyMailSender implements MailSender {

  @Override
  public void send(SimpleMailMessage simpleMessage) throws MailException {
    System.out.println(simpleMessage);
  }

  @Override
  public void send(SimpleMailMessage... simpleMessages) throws MailException {
    for (SimpleMailMessage simpleMessage : simpleMessages) {
      System.out.println(simpleMessage);
    }
  }
}
