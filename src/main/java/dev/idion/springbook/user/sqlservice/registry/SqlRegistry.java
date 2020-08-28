package dev.idion.springbook.user.sqlservice.registry;

import dev.idion.springbook.user.sqlservice.exception.SqlNotFoundException;

public interface SqlRegistry {

  void registerSql(String key, String sql);

  String findSql(String key) throws SqlNotFoundException;
}
