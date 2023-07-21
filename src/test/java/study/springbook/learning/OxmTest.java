package study.springbook.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ContextConfiguration;
import study.springbook.sqlservice.jaxb.SqlType;
import study.springbook.sqlservice.jaxb.Sqlmap;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(locations = "/oxmTest-applicationContext.xml")
public class OxmTest {

    @Autowired
    private Unmarshaller unmarshaller;

    @Test
    public void unmarshallSqlMap() throws IOException {
        StreamSource xmlSource = new StreamSource(getClass().getResourceAsStream("/sqlmap.xml"));

        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(xmlSource);

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
