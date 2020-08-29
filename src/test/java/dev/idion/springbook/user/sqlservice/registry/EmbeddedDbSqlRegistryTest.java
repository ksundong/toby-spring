package dev.idion.springbook.user.sqlservice.registry;

import org.junit.jupiter.api.AfterEach;
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
}
