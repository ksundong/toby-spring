package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.domain.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

  @Autowired
  private JdbcContext jdbcContext;

  @Autowired
  private DataSource dataSource;

  public void setJdbcContext(JdbcContext jdbcContext) {
    this.jdbcContext = jdbcContext;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void add(User user) throws SQLException {
    jdbcContext.workWithStatementStrategy(c -> {
      PreparedStatement ps = c
          .prepareStatement("insert into USER(id, name, password) VALUES (?,?,?)");
      ps.setString(1, user.getId());
      ps.setString(2, user.getName());
      ps.setString(3, user.getPassword());
      return ps;
    });
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

  public void deleteAll() throws SQLException {
    jdbcContext.workWithStatementStrategy(c -> c.prepareStatement("delete from USER"));
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
