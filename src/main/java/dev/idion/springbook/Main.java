package dev.idion.springbook;

import dev.idion.springbook.user.dao.DaoFactory;
import dev.idion.springbook.user.domain.User;
import dev.idion.springbook.user.service.UserService;
import java.sql.SQLException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

  public static void main(String[] args) throws SQLException {
    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
    UserService userService = context.getBean(UserService.class);

    String id = "whiteship";
    userService.addUser(id, "백기선", "married");

    User user2 = userService.getUser(id);
    System.out.println(user2.getName());
    System.out.println(user2.getPassword());

    System.out.println(user2.getId() + " 조회 성공");
  }
}
