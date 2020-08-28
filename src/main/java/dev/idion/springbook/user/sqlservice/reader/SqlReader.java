package dev.idion.springbook.user.sqlservice.reader;

import dev.idion.springbook.user.sqlservice.registry.SqlRegistry;

public interface SqlReader {

  void read(SqlRegistry sqlRegistry);
}
