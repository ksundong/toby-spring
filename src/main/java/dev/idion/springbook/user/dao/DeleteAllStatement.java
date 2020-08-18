package dev.idion.springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllStatement implements StatementStrategy {

  @Override
  public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
    try (PreparedStatement ps = c.prepareStatement("delete from USER")) {
      return ps;
    }
  }
}
