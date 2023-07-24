package study.springbook.sqlservice.updatable;

import study.springbook.exception.SqlUpdateFailureException;
import study.springbook.sqlservice.SqlRegistry;

import java.util.Map;

public interface UpdatableSqlRegistry extends SqlRegistry {

    void updateSql(String key, String sql) throws SqlUpdateFailureException;

    void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException;
}
