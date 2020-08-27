package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import dev.idion.springbook.user.sqlservice.SqlService;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoJdbc implements UserDao {

  private final SqlService sqlService;
  private final JdbcOperations jdbcOperations;
  private final RowMapper<User> userMapper = (rs, rowNum) -> {
    User user = new User();
    user.setId(rs.getString("id"));
    user.setName(rs.getString("name"));
    user.setPassword(rs.getString("password"));
    user.setLogin(rs.getInt("login"));
    user.setRecommend(rs.getInt("recommend"));
    user.setLevel(Level.valueOf(rs.getString("level")));
    user.setEmail(rs.getString("email"));
    return user;
  };

  public UserDaoJdbc(SqlService sqlService, JdbcOperations jdbcOperations) {
    this.sqlService = sqlService;
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public void add(User user) throws DuplicateKeyException {
    this.jdbcOperations.update(this.sqlService.getSql("userAdd"),
        user.getId(), user.getName(), user.getPassword(), user.getLogin(), user.getRecommend(),
        user.getLevel().toString(), user.getEmail());
  }

  @Override
  public User get(String id) {
    return this.jdbcOperations
        .queryForObject(this.sqlService.getSql("userGet"), new Object[]{id}, this.userMapper);
  }

  @Override
  public void deleteAll() {
    this.jdbcOperations.update(this.sqlService.getSql("userDeleteAll"));
  }

  @Override
  public Integer getCount() {
    return this.jdbcOperations
        .queryForObject(this.sqlService.getSql("userGetCount"), Integer.class);
  }

  @Override
  public List<User> getAll() {
    return this.jdbcOperations.query(this.sqlService.getSql("userGetAll"), this.userMapper);
  }

  @Override
  public void update(User user) {
    this.jdbcOperations.update(this.sqlService.getSql("userUpdate"),
        user.getName(), user.getPassword(), user.getLogin(), user.getRecommend(),
        user.getLevel().toString(), user.getEmail(), user.getId());
  }
}
