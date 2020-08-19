package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

  private final JdbcOperations jdbcOperations;
  private final RowMapper<User> userMapper = (rs, rowNum) -> {
    User user = new User();
    user.setId(rs.getString("id"));
    user.setName(rs.getString("name"));
    user.setPassword(rs.getString("password"));
    return user;
  };

  public UserDao(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  public void add(User user) {
    jdbcOperations.update("insert into USER(id, name, password) VALUES (?,?,?)"
        , user.getId(), user.getName(), user.getPassword());
  }

  public User get(String id) {
    return this.jdbcOperations.queryForObject("select * from USER where id = ?"
        , new Object[]{id}, this.userMapper);
  }

  public void deleteAll() {
    this.jdbcOperations.update("delete from USER");
  }

  public Integer getCount() {
    return this.jdbcOperations.queryForObject("select count(*) from USER", Integer.class);
  }

  public List<User> getAll() {
    return this.jdbcOperations.query("select * from USER order by id", this.userMapper);
  }
}
