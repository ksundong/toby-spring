package dev.idion.springbook.user.sqlservice.registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConcurrentHashMapSqlRegistryTest {

  UpdatableSqlRegistry sqlRegistry;

  @BeforeEach
  void setUp() {
    sqlRegistry = new ConcurrentHashMapSqlRegistry();
    sqlRegistry.registerSql("KEY1", "SQL1");
    sqlRegistry.registerSql("KEY2", "SQL2");
    sqlRegistry.registerSql("KEY3", "SQL3");
  }

  @Test
  void find() {
    checkFindResult("SQL1", "SQL2", "SQL3");
  }

  private void checkFindResult(String expected1, String expected2, String expected3) {
    assertThat(sqlRegistry.findSql("KEY1")).isEqualTo(expected1);
    assertThat(sqlRegistry.findSql("KEY2")).isEqualTo(expected2);
    assertThat(sqlRegistry.findSql("KEY3")).isEqualTo(expected3);
  }

  @Test
  void unknownKey() {
    assertThatThrownBy(() -> sqlRegistry.findSql("SQL9999!!!@#$"));
  }

  @Test
  void updateSingle() {
    sqlRegistry.updateSql("KEY2", "Modified2");
    checkFindResult("SQL1", "Modified2", "SQL3");
  }

  @Test
  void updateMulti() {
    Map<String, String> sqlmap = new HashMap<>();
    sqlmap.put("KEY1", "Modified1");
    sqlmap.put("KEY3", "Modified3");

    sqlRegistry.updateSql(sqlmap);
    checkFindResult("Modified1", "SQL2", "Modified3");
  }

  @Test
  void updateWithNotExistingKey() {
    assertThatThrownBy(() -> sqlRegistry.updateSql("SQL9999!@#$", "Modified2"));
  }
}
