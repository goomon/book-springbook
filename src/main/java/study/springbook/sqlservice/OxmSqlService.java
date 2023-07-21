package study.springbook.sqlservice;

import jakarta.annotation.PostConstruct;
import org.springframework.oxm.Unmarshaller;
import study.springbook.exception.SqlNotFoundException;
import study.springbook.exception.SqlRetrievalFailureException;
import study.springbook.sqlservice.jaxb.SqlType;
import study.springbook.sqlservice.jaxb.Sqlmap;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class OxmSqlService implements SqlService {

    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();

    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        oxmSqlReader.setUnmarshaller(unmarshaller);
    }

    public void setSqlmapFile(String sqlmapFile) {
        oxmSqlReader.setSqlmapFile(sqlmapFile);
    }

    @PostConstruct
    public void loadSql() {
        oxmSqlReader.read(sqlRegistry);
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        try {
            return sqlRegistry.findSql(key);
        } catch (SqlNotFoundException e) {
            throw new SqlRetrievalFailureException("Cannot find " + key, e);
        }
    }

    private class OxmSqlReader implements SqlReader {

        private Unmarshaller unmarshaller;
        private String sqlmapFile;

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }

        public void setSqlmapFile(String sqlmapFile) {
            this.sqlmapFile = sqlmapFile;
        }

        @Override
        public void read(SqlRegistry sqlRegistry) {
            try {
                StreamSource source = new StreamSource(getClass().getResourceAsStream(sqlmapFile));
                Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(source);

                for (SqlType sql : sqlmap.getSql()) {
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot load " + sqlmapFile, e);
            }
        }
    }
}

