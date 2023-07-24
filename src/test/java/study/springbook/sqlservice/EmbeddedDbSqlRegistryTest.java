package study.springbook.sqlservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.ContextConfiguration;
import study.springbook.TestApplicationContext;
import study.springbook.exception.SqlUpdateFailureException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.*;

@ContextConfiguration(classes = TestApplicationContext.class)
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

    @Test
    public void transactionalUpdate() {
        checkFindResult("sql1", "sql2", "sql3");

        Map<String, String> sqlmap = new HashMap<>();
        sqlmap.put("key1", "modified1");
        sqlmap.put("unknown", "unknown");

        try {
            sqlRegistry.updateSql(sqlmap);
        } catch (SqlUpdateFailureException e) {
            checkFindResult("sql1", "sql2", "sql3");
        }
    }
}
