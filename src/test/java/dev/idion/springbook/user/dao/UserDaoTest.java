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

    String id = "gyumee";
    String name = "박성철";
    String married = "springno1";
    User user1 = new User(id, name, married);

    userService.addUser(user1);
    assertEquals(1, userService.countUsers());

    User user2 = userService.getUser(id);

    assertEquals(user1.getName(), user2.getName());
    assertEquals(user1.getPassword(), user2.getPassword());
  }
}
