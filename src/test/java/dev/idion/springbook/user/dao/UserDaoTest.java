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

    userService.deleteUsers();
    assertEquals(0, userService.countUsers());

    User user1 = new User("gyumee", "박성철", "springno1");

    userService.addUser(user1);
    assertEquals(1, userService.countUsers());

    User user2 = userService.getUser(user1.getId());

    assertEquals(user1.getName(), user2.getName());
    assertEquals(user1.getPassword(), user2.getPassword());
  }

  @Test
  void count() throws SQLException {
    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
    UserService userService = context.getBean(UserService.class);

    User user1 = new User("gyumee", "박성철", "springno1");
    User user2 = new User("leegw700", "이길원", "springno2");
    User user3 = new User("bumjin", "박범진", "springno3");

    userService.deleteUsers();
    assertEquals(0, userService.countUsers());

    userService.addUser(user1);
    assertEquals(1, userService.countUsers());

    userService.addUser(user2);
    assertEquals(2, userService.countUsers());

    userService.addUser(user3);
    assertEquals(3, userService.countUsers());
  }
}
