package dev.idion.springbook.learningtest.spring.oxm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class OxmTestConfig {

  @Bean
  public Jaxb2Marshaller unmarshaller() {
    Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
    unmarshaller.setContextPath("dev.idion.springbook.user.sqlservice.jaxb");
    return unmarshaller;
  }
}
