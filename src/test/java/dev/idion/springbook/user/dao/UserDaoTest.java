package dev.idion.springbook.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import dev.idion.springbook.user.service.UserService;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

class UserDaoTest {

  private UserService userService;

  private User user1;
  private User user2;
  private User user3;

  @BeforeEach
  void setUp() {
    DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost:3306/testdb",
        "spring", "book", true);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    UserDao userDao = new UserDaoJdbc(jdbcTemplate);

    this.userService = new UserService(userDao);
    this.user1 = new User("gyumee", "박성철", "springno1", 1, 0, Level.BASIC);
    this.user2 = new User("leegw700", "이길원", "springno2", 55, 10, Level.SILVER);
    this.user3 = new User("bumjin", "박범진", "springno3", 100, 40, Level.GOLD);
  }

  @Test
  void addAndGet() {
    this.userService.deleteUsers();
    assertEquals(0, this.userService.countUsers());

    this.userService.addUser(this.user1);
    this.userService.addUser(this.user2);
    assertEquals(2, this.userService.countUsers());

    User userGet1 = this.userService.getUser(this.user1.getId());
    assertThat(userGet1).isEqualToComparingFieldByField(this.user1);

    User userGet2 = this.userService.getUser(this.user2.getId());
    assertThat(userGet2).isEqualToComparingFieldByField(this.user2);
  }

  @Test
  void duplicateUserException() {
    this.userService.deleteUsers();
    assertEquals(0, this.userService.countUsers());

    assertThatThrownBy(() -> {
      this.userService.addUser(this.user1);
      this.userService.addUser(this.user1);
    }).isInstanceOf(DuplicateKeyException.class);
  }

  @Test
  void count() {
    this.userService.deleteUsers();
    assertEquals(0, this.userService.countUsers());

    this.userService.addUser(this.user1);
    assertEquals(1, this.userService.countUsers());

    this.userService.addUser(this.user2);
    assertEquals(2, this.userService.countUsers());

    this.userService.addUser(user3);
    assertEquals(3, this.userService.countUsers());
  }

  @Test
  void getUserFailure() {
    assertThatThrownBy(() -> {
      this.userService.deleteUsers();
      assertEquals(0, this.userService.countUsers());
      this.userService.getUser("unknown_id");
    }).isInstanceOf(EmptyResultDataAccessException.class);
  }

  @Test
  void getAll() {
    userService.deleteUsers();

    List<User> users0 = userService.getAll();
    assertThat(users0).hasSize(0);

    userService.addUser(user1);
    List<User> users1 = userService.getAll();
    assertThat(users1).hasSize(1).usingFieldByFieldElementComparator().contains(user1);
    assertThat(users1.get(0)).isEqualToComparingFieldByField(user1);

    userService.addUser(user2);
    List<User> users2 = userService.getAll();
    assertThat(users2).hasSize(2).usingFieldByFieldElementComparator().contains(user1, user2);
    assertThat(users2.get(0)).isEqualToComparingFieldByField(user1);
    assertThat(users2.get(1)).isEqualToComparingFieldByField(user2);

    userService.addUser(user3);
    List<User> users3 = userService.getAll();
    assertThat(users3).hasSize(3).usingFieldByFieldElementComparator()
        .contains(user1, user2, user3);
    assertThat(users3.get(0)).isEqualToComparingFieldByField(user3);
    assertThat(users3.get(1)).isEqualToComparingFieldByField(user1);
    assertThat(users3.get(2)).isEqualToComparingFieldByField(user2);
  }
}
