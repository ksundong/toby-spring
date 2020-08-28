package dev.idion.springbook.learningtest.spring.embeddeddb;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

class EmbeddedDbTest {

  EmbeddedDatabase db;
  JdbcTemplate template;

  @BeforeEach
  void setUp() {
    db = new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:/schema.sql")
        .addScript("classpath:/data.sql")
        .build();

    template = new JdbcTemplate(db);
  }

  @AfterEach
  void tearDown() {
    db.shutdown();
  }

  @Test
  void initData() {
    assertThat(template.queryForObject("select count(*) from sqlmap", Integer.class)).isEqualTo(2);

    List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
    assertThat(list.get(0).get("key_")).isEqualTo("KEY1");
    assertThat(list.get(0).get("sql_")).isEqualTo("SQL1");
    assertThat(list.get(1).get("key_")).isEqualTo("KEY2");
    assertThat(list.get(1).get("sql_")).isEqualTo("SQL2");
  }
}
