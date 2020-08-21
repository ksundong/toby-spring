package dev.idion.springbook.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import dev.idion.springbook.user.dao.TestDaoFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestDaoFactory.class)
class UserServiceTest {

  @Autowired
  UserService userService;

  @Test
  void bean() {
    assertThat(userService).isNotNull();
  }
}
