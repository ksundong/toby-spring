package dev.idion.springbook.user.sqlservice.registry;

class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

  @Override
  protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
    return new ConcurrentHashMapSqlRegistry();
  }
}
