package dev.idion.springbook.user.sqlservice.registry;

import dev.idion.springbook.user.sqlservice.exception.SqlNotFoundException;
import dev.idion.springbook.user.sqlservice.exception.SqlUpdateFailureException;
import java.util.Map;
import java.util.Map.Entry;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {

  private final JdbcTemplate jdbc;

  public EmbeddedDbSqlRegistry(DataSource embeddedDatabase) {
    this.jdbc = new JdbcTemplate(embeddedDatabase);
  }

  @Override
  public void updateSql(String key, String sql) throws SqlUpdateFailureException {
    int affected = jdbc.update("update sqlmap set sql_ = ? where key_ = ?", sql, key);
    if (affected == 0) {
      throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
    }
  }

  @Override
  public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
    for (Entry<String, String> entry : sqlmap.entrySet()) {
      updateSql(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void registerSql(String key, String sql) {
    jdbc.update("insert into sqlmap(key_, sql_) values(?, ?)", key, sql);
  }

  @Override
  public String findSql(String key) throws SqlNotFoundException {
    try {
      return jdbc.queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
    } catch (EmptyResultDataAccessException e) {
      throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다.", e);
    }
  }
}
