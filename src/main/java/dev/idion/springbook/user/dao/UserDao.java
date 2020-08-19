package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.domain.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

  private final JdbcTemplate jdbcTemplate;

  public UserDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void add(User user) {
    jdbcTemplate.update("insert into USER(id, name, password) VALUES (?,?,?)"
        , user.getId(), user.getName(), user.getPassword());
  }

  public User get(String id) {
    return this.jdbcTemplate.queryForObject("select * from USER where id = ?", new Object[]{id}
        , (rs, rowNum) -> {
          User user = new User();
          user.setId(rs.getString("id"));
          user.setName(rs.getString("name"));
          user.setPassword(rs.getString("password"));
          return user;
        });
  }

  public void deleteAll() {
    this.jdbcTemplate.update("delete from USER");
  }

  public Integer getCount() {
    return this.jdbcTemplate.queryForObject("select count(*) from USER", Integer.class);
  }
}
