package dev.idion.springbook.user.sqlservice.registry;

import static org.junit.jupiter.api.Assertions.fail;

import dev.idion.springbook.user.sqlservice.exception.SqlUpdateFailureException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

  EmbeddedDatabase db;

  @Override
  protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
    db = new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:/schema.sql")
        .build();

    return new EmbeddedDbSqlRegistry(db);
  }

  @AfterEach
  void tearDown() {
    db.shutdown();
  }

  @Test
  void transactionalUpdate() {
    checkFindResult("SQL1", "SQL2", "SQL3");

    Map<String, String> sqlmap = new HashMap<>();
    sqlmap.put("KEY1", "Modified1");
    sqlmap.put("KEY9999!@#$", "Modified9999");

    try {
      sqlRegistry.updateSql(sqlmap);
      fail();
    } catch (SqlUpdateFailureException e) {
    }

    checkFindResult("SQL1", "SQL2", "SQL3");
  }
}
