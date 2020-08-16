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
    String name = "백기선";
    String married = "married";
    User user1 = new User(id, name, married);
    userService.addUser(user1);

    User user2 = userService.getUser(id);
    if (!user1.getName().equals(user2.getName())) {
      System.out.println("테스트 실패(name)");
    } else if (!user1.getPassword().equals(user2.getPassword())) {
      System.out.println("테스트 실패(password)");
    } else {
      System.out.println("조회 테스트 성공");
    }
  }
}
