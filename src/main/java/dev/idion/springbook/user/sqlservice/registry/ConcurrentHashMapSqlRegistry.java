package dev.idion.springbook.user.sqlservice.registry;

import dev.idion.springbook.user.sqlservice.exception.SqlNotFoundException;
import dev.idion.springbook.user.sqlservice.exception.SqlUpdateFailureException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {

  private Map<String, String> sqlMap = new ConcurrentHashMap<>();

  @Override
  public void updateSql(String key, String sql) throws SqlUpdateFailureException {
    if (sqlMap.get(key) == null) {
      throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
    }
    sqlMap.put(key, sql);
  }

  @Override
  public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
    for (Entry<String, String> entry : sqlmap.entrySet()) {
      updateSql(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void registerSql(String key, String sql) {
    sqlMap.put(key, sql);
  }

  @Override
  public String findSql(String key) throws SqlNotFoundException {
    String sql = sqlMap.get(key);
    if (sql == null) {
      throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
    }
    return sql;
  }
}
