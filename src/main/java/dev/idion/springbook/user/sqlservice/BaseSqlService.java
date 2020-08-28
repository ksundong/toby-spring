package dev.idion.springbook.user.sqlservice;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class BaseSqlService implements SqlService {

  protected final SqlReader sqlReader;
  protected final SqlRegistry sqlRegistry;

  public BaseSqlService(SqlReader sqlReader,
      SqlRegistry sqlRegistry) {
    this.sqlReader = sqlReader;
    this.sqlRegistry = sqlRegistry;
  }

  @PostConstruct
  public void loadSql() {
    this.sqlReader.read(this.sqlRegistry);
  }

  @Override
  public String getSql(String key) throws SqlRetrievalFailureException {
    try {
      return this.sqlRegistry.findSql(key);
    } catch (SqlNotFoundException e) {
      throw new SqlRetrievalFailureException(e);
    }
  }
}
