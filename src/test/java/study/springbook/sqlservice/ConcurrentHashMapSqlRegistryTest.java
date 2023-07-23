package study.springbook.sqlservice;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    @Override
    protected UpdatableSqlRegistry createUpdatableRegistry() {
        UpdatableSqlRegistry sqlRegistry = new ConcurrentHashMapSqlRegistry();
        return sqlRegistry;
    }
}
