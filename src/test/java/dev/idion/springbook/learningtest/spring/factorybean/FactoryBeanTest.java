package dev.idion.springbook.learningtest.spring.factorybean;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
class FactoryBeanTest {

  public static final String FACTORY_BEAN = "Factory Bean";

  @Autowired
  ApplicationContext context;

  @Test
  void getMessageFromFactoryBean() {
    Object message = context.getBean("message");
    assertThat(message.getClass()).isEqualTo(Message.class);
    assertThat(message).isOfAnyClassIn(Message.class);
    assertThat(((Message) message).getText()).isEqualTo(FACTORY_BEAN);
  }

  @Test
  void getFactoryBean() {
    Object factory = context.getBean("&message");
    assertThat(factory.getClass()).isEqualTo(MessageFactoryBean.class);
  }

  @Configuration
  static class FactoryBeanTestContext {

    @Bean
    public MessageFactoryBean message() {
      return new MessageFactoryBean(FACTORY_BEAN);
    }
  }
}
