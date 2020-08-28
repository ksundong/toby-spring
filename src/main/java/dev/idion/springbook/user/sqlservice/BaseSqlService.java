package dev.idion.springbook.user.sqlservice;

import dev.idion.springbook.user.sqlservice.exception.SqlNotFoundException;
import dev.idion.springbook.user.sqlservice.exception.SqlRetrievalFailureException;
import dev.idion.springbook.user.sqlservice.reader.SqlReader;
import dev.idion.springbook.user.sqlservice.registry.SqlRegistry;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseSqlService implements SqlService {

  @Autowired
  protected SqlReader sqlReader;

  @Autowired
  protected SqlRegistry sqlRegistry;

  public void setSqlReader(SqlReader sqlReader) {
    this.sqlReader = sqlReader;
  }

  public void setSqlRegistry(SqlRegistry sqlRegistry) {
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
