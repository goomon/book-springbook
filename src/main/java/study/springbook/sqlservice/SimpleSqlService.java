package study.springbook.sqlservice;

import study.springbook.exception.SqlRetrievalFailureException;
import study.springbook.sqlservice.SqlService;

import java.util.Map;

public class SimpleSqlService implements SqlService {

    private Map<String, String> sqlMap;

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if (sql == null) {
            throw new SqlRetrievalFailureException("Cannot find " + key);
        } else {
            return sql;
        }
    }
}
