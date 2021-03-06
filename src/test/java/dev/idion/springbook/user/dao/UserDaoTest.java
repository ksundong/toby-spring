package dev.idion.springbook.user.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import dev.idion.springbook.user.sqlservice.SimpleSqlService;
import dev.idion.springbook.user.sqlservice.SqlService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

class UserDaoTest {

  private UserDao userDao;

  private User user1;
  private User user2;
  private User user3;

  @BeforeEach
  void setUp() {
    DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost:3306/testdb",
        "spring", "book", true);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    Map<String, String> sqlMap = new HashMap<>();
    sqlMap.put("userAdd",
        "insert into USER(id, name, password, login, recommend, level, email) VALUES (?,?,?,?,?,?,?)");
    sqlMap.put("userGet", "select * from USER where id = ?");
    sqlMap.put("userDeleteAll", "delete from USER");
    sqlMap.put("userGetCount", "select count(*) from USER");
    sqlMap.put("userGetAll", "select * from USER order by id");
    sqlMap.put("userUpdate",
        "update USER set name = ?, password = ?, login = ?, recommend = ?, level = ?, email = ? where id = ?");
    SqlService sqlService = new SimpleSqlService(sqlMap);

    userDao = new UserDaoJdbc(sqlService, jdbcTemplate);

    this.user1 = new User("gyumee", "박성철", "springno1", 1, 0, Level.BASIC,
        "gyumee@springbook.test");
    this.user2 = new User("leegw700", "이길원", "springno2", 55, 10, Level.SILVER,
        "leegw700@springbook.test");
    this.user3 = new User("bumjin", "박범진", "springno3", 100, 40, Level.GOLD,
        "bumjin@springbook.test");
  }

  @Test
  void addAndGet() {
    this.userDao.deleteAll();
    assertEquals(0, this.userDao.getCount());

    this.userDao.add(this.user1);
    this.userDao.add(this.user2);
    assertEquals(2, this.userDao.getCount());

    User userGet1 = this.userDao.get(this.user1.getId());
    assertThat(userGet1).isEqualToComparingFieldByField(this.user1);

    User userGet2 = this.userDao.get(this.user2.getId());
    assertThat(userGet2).isEqualToComparingFieldByField(this.user2);
  }

  @Test
  void duplicateUserException() {
    this.userDao.deleteAll();
    assertEquals(0, this.userDao.getCount());

    assertThatThrownBy(() -> {
      this.userDao.add(this.user1);
      this.userDao.add(this.user1);
    }).isInstanceOf(DuplicateKeyException.class);
  }

  @Test
  void count() {
    this.userDao.deleteAll();
    assertEquals(0, this.userDao.getCount());

    this.userDao.add(this.user1);
    assertEquals(1, this.userDao.getCount());

    this.userDao.add(this.user2);
    assertEquals(2, this.userDao.getCount());

    this.userDao.add(user3);
    assertEquals(3, this.userDao.getCount());
  }

  @Test
  void getUserFailure() {
    assertThatThrownBy(() -> {
      this.userDao.deleteAll();
      assertEquals(0, this.userDao.getCount());
      this.userDao.get("unknown_id");
    }).isInstanceOf(EmptyResultDataAccessException.class);
  }

  @Test
  void getAll() {
    userDao.deleteAll();

    List<User> users0 = userDao.getAll();
    assertThat(users0).hasSize(0);

    userDao.add(user1);
    List<User> users1 = userDao.getAll();
    assertThat(users1).hasSize(1).usingFieldByFieldElementComparator().contains(user1);
    assertThat(users1.get(0)).isEqualToComparingFieldByField(user1);

    userDao.add(user2);
    List<User> users2 = userDao.getAll();
    assertThat(users2).hasSize(2).usingFieldByFieldElementComparator().contains(user1, user2);
    assertThat(users2.get(0)).isEqualToComparingFieldByField(user1);
    assertThat(users2.get(1)).isEqualToComparingFieldByField(user2);

    userDao.add(user3);
    List<User> users3 = userDao.getAll();
    assertThat(users3).hasSize(3).usingFieldByFieldElementComparator()
        .contains(user1, user2, user3);
    assertThat(users3.get(0)).isEqualToComparingFieldByField(user3);
    assertThat(users3.get(1)).isEqualToComparingFieldByField(user1);
    assertThat(users3.get(2)).isEqualToComparingFieldByField(user2);
  }

  @Test
  void update() {
    userDao.deleteAll();
    userDao.add(user1);
    userDao.add(user2);

    user1.setName("오민규");
    user1.setPassword("springno6");
    user1.setLogin(1000);
    user1.setRecommend(999);
    user1.setLevel(Level.GOLD);
    user1.setEmail("mingoo@springbook.test");
    userDao.update(user1);

    User user1update = userDao.get(user1.getId());
    assertThat(user1update).isEqualToComparingFieldByField(user1);

    User user2same = userDao.get(user2.getId());
    assertThat(user2same).isEqualToComparingFieldByField(user2);
  }
}
