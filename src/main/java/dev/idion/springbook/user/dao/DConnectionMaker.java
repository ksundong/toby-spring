package dev.idion.springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.stereotype.Service;

@Service
public class DConnectionMaker implements ConnectionMaker {

  @Override
  public Connection makeConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection c = DriverManager
        .getConnection("jdbc:mysql://localhost:3306/springbook", "spring", "book");
    return c;
  }
}
