package study.springbook.learning;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Test;
import study.springbook.sqlservice.jaxb.SqlType;
import study.springbook.sqlservice.jaxb.Sqlmap;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class JaxbTest {

    @Test
    public void readSqlmap() throws JAXBException {
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext.newInstance(contextPath);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("/sqlmap.xml"));

        List<SqlType> sqlList = sqlmap.getSql();

        assertThat(sqlList.size()).isEqualTo(6);
        assertThat(sqlList.get(0).getKey()).isEqualTo("memberAdd");
        assertThat(sqlList.get(1).getKey()).isEqualTo("memberGet");
        assertThat(sqlList.get(2).getKey()).isEqualTo("memberGetAll");
        assertThat(sqlList.get(3).getKey()).isEqualTo("memberDeleteAll");
        assertThat(sqlList.get(4).getKey()).isEqualTo("memberGetCount");
        assertThat(sqlList.get(5).getKey()).isEqualTo("memberUpdate");
    }
}
