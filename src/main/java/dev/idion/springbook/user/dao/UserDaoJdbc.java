package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.domain.Level;
import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoJdbc implements UserDao {

  private final JdbcOperations jdbcOperations;
  private final RowMapper<User> userMapper = (rs, rowNum) -> {
    User user = new User();
    user.setId(rs.getString("id"));
    user.setName(rs.getString("name"));
    user.setPassword(rs.getString("password"));
    user.setLogin(rs.getInt("login"));
    user.setRecommend(rs.getInt("recommend"));
    user.setLevel(Level.valueOf(rs.getString("level")));
    return user;
  };

  public UserDaoJdbc(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public void add(User user) throws DuplicateKeyException {
    this.jdbcOperations
        .update("insert into USER(id, name, password, login, recommend, level) VALUES (?,?,?,?,?,?)"
            , user.getId(), user.getName(), user.getPassword(), user.getLogin(),
            user.getRecommend(), user.getLevel().toString());
  }

  @Override
  public User get(String id) {
    return this.jdbcOperations.queryForObject("select * from USER where id = ?"
        , new Object[]{id}, this.userMapper);
  }

  @Override
  public void deleteAll() {
    this.jdbcOperations.update("delete from USER");
  }

  @Override
  public Integer getCount() {
    return this.jdbcOperations.queryForObject("select count(*) from USER", Integer.class);
  }

  @Override
  public List<User> getAll() {
    return this.jdbcOperations.query("select * from USER order by id", this.userMapper);
  }

  @Override
  public void update(User user) {
    this.jdbcOperations.update(
        "update USER set name = ?, password = ?, login = ?, recommend = ?, level = ? where id = ?",
        user.getName(), user.getPassword(), user.getLogin(), user.getRecommend(),
        user.getLevel().toString(), user.getId());
  }
}
