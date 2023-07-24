package study.springbook.sqlservice;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MemberSqlMapConfig implements SqlMapConfig {

    @Override
    public Resource getSqlMapResource() {
        return new ClassPathResource("/sql/sqlmap.xml");
    }
}
