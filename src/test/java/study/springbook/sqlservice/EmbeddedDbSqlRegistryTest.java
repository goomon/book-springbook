package study.springbook.sqlservice;

import org.junit.jupiter.api.AfterEach;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.*;

@ContextConfiguration(locations = "/test-applicationContext.xml")
public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    private EmbeddedDatabase embeddedDatabase;

    @Override
    protected UpdatableSqlRegistry createUpdatableRegistry() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .setType(H2)
                .addScript("classpath:/sql/embedded/schema.sql")
                .build();
        EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
        embeddedDbSqlRegistry.setDatasource(embeddedDatabase);

        return embeddedDbSqlRegistry;
    }

    @AfterEach
    public void tearDown() {
        embeddedDatabase.shutdown();
    }
}
