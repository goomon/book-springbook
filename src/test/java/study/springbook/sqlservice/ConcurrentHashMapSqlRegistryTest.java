package study.springbook.sqlservice;

import study.springbook.sqlservice.updatable.ConcurrentHashMapSqlRegistry;
import study.springbook.sqlservice.updatable.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    @Override
    protected UpdatableSqlRegistry createUpdatableRegistry() {
        UpdatableSqlRegistry sqlRegistry = new ConcurrentHashMapSqlRegistry();
        return sqlRegistry;
    }
}
