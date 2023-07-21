package study.springbook.sqlservice;

import jakarta.annotation.PostConstruct;

public class DefaultSqlService extends BaseSqlService {

    @Override
    @PostConstruct
    public void loadSql() {
        if (this.getSqlReader() == null) {
            setSqlReader(new JaxbXmlSqlReader());
        }
        if (this.getSqlRegistry() == null) {
            setSqlRegistry(new HashMapSqlRegistry());
        }
        super.loadSql();
    }
}
