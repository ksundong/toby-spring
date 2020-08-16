package dev.idion.springbook.user.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.idion.springbook.user.domain.User;
import dev.idion.springbook.user.service.UserService;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class UserDaoTest {

  @Test
  void addAndGet() throws SQLException {
    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
    UserService userService = context.getBean(UserService.class);

    String id = "gyumee";
    String name = "박성철";
    String married = "springno1";
    User user1 = new User(id, name, married);

    userService.addUser(user1);

    User user2 = userService.getUser(id);

    assertEquals(user2.getName(), user1.getName());
    assertEquals(user2.getPassword(), user1.getPassword());
  }
}
