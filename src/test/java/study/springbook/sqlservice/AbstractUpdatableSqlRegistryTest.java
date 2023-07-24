package study.springbook.sqlservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import study.springbook.exception.SqlUpdateFailureException;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public abstract class AbstractUpdatableSqlRegistryTest {

    UpdatableSqlRegistry sqlRegistry;

    @BeforeEach
    public void setUp() {
        sqlRegistry = createUpdatableRegistry();
        sqlRegistry.registerSql("key1", "sql1");
        sqlRegistry.registerSql("key2", "sql2");
        sqlRegistry.registerSql("key3", "sql3");
    }

    abstract protected UpdatableSqlRegistry createUpdatableRegistry();

    @Test
    public void find() {
        checkFindResult("sql1", "sql2", "sql3");
    }

    @Test
    public void unknownKey() {
        assertThatThrownBy(() -> sqlRegistry.updateSql("unknown", "modified")).isInstanceOf(SqlUpdateFailureException.class);
    }

    @Test
    public void updateSingle() {
        sqlRegistry.updateSql("key2", "modified2");
        checkFindResult("sql1", "modified2", "sql3");
    }

    @Test
    public void updateMultiple() {
        Map<String, String> sqlmap = new HashMap<>();
        sqlmap.put("key1", "modified1");
        sqlmap.put("key2", "modified2");

        sqlRegistry.updateSql(sqlmap);
        checkFindResult("modified1", "modified2", "sql3");
    }

    @Test
    public void updateWithNotExistingKey() {
        assertThatThrownBy(() -> sqlRegistry.updateSql("unknown", "modified"));
    }

    protected void checkFindResult(String expected1, String expected2, String expected3) {
        assertThat(sqlRegistry.findSql("key1")).isEqualTo(expected1);
        assertThat(sqlRegistry.findSql("key2")).isEqualTo(expected2);
        assertThat(sqlRegistry.findSql("key3")).isEqualTo(expected3);
    }
}
