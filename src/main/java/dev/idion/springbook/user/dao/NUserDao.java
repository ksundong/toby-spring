package dev.idion.springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NUserDao extends UserDao {

  // N사 DB connection 생성 코드
  @Override
  public Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection c = DriverManager
        .getConnection("jdbc:mysql://localhost:3306/springbook", "spring", "book");
    return c;
  }
}
