package dev.idion.springbook.user.sqlservice;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SimpleSqlService implements SqlService {

  private final Map<String, String> sqlMap;

  public SimpleSqlService(Map<String, String> sqlMap) {
    this.sqlMap = sqlMap;
  }

  @Override
  public String getSql(String key) throws SqlRetrievalFailureException {
    String sql = sqlMap.get(key);
    if (sql == null) {
      throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
    }
    return sql;
  }
}
