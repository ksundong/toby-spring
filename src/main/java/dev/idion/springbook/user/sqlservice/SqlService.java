package dev.idion.springbook.user.sqlservice;

import dev.idion.springbook.user.sqlservice.exception.SqlRetrievalFailureException;

public interface SqlService {

  String getSql(String key) throws SqlRetrievalFailureException;
}
