package study.springbook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import study.springbook.domain.Member;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
class MemberDaoTest {

    @Autowired
    private MemberDao dao;
    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    public void setUp() {
        member1 = new Member("id1", "name1", "password1");
        member2 = new Member("id2", "name2", "password2");
        member3 = new Member("id3", "name3", "password3");

        DataSource dataSource = new SingleConnectionDataSource(
                "jdbc:postgresql://localhost:5432/test_db", "postgres", "postgres", true);
        dao.setDataSource(dataSource);
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.add(member1);
        assertEquals(dao.getCount(), 1);

        dao.add(member2);
        assertEquals(dao.getCount(), 2);

        dao.add(member3);
        assertEquals(dao.getCount(), 3);

        Member data1 = dao.get(member1.getId());
        assertEquals(data1.getName(), member1.getName());
        assertEquals(data1.getPassword(), member1.getPassword());

        Member data2 = dao.get(member2.getId());
        assertEquals(data2.getName(), member2.getName());
        assertEquals(data2.getPassword(), member2.getPassword());
    }

    @Test
    public void getUserFailure() throws SQLException {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        assertThrows(EmptyResultDataAccessException.class, () -> {
            dao.get("unknown");
        });
    }
}