package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.domain.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

  private final JdbcTemplate jdbcTemplate;
  private final DataSource dataSource;

  public UserDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
    this.jdbcTemplate = jdbcTemplate;
    this.dataSource = dataSource;
  }

  public void add(User user) {
    jdbcTemplate.update("insert into USER(id, name, password) VALUES (?,?,?)"
        , user.getId(), user.getName(), user.getPassword());
  }

  public User get(String id) throws SQLException {
    Connection c = dataSource.getConnection();

    User user = null;
    try (PreparedStatement ps = c.prepareStatement("select * from USER where id = ?")) {
      ps.setString(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          user = new User();
          user.setId(rs.getString("id"));
          user.setName(rs.getString("name"));
          user.setPassword(rs.getString("password"));
        }
      }
    }
    c.close();

    if (user == null) {
      throw new EmptyResultDataAccessException(1);
    }

    return user;
  }

  public void deleteAll() {
    this.jdbcTemplate.update("delete from USER");
  }

  public int getCount() throws SQLException {
    try (Connection c = dataSource.getConnection()) {
      try (PreparedStatement ps = c.prepareStatement("select count(*) from USER")) {
        try (ResultSet rs = ps.executeQuery()) {
          rs.next();
          return rs.getInt(1);
        }
      }
    } catch (SQLException e) {
      throw e;
    }
  }
}
