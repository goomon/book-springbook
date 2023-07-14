package study.springbook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import study.springbook.domain.Member;
import study.springbook.factory.TestDaoFactory;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = TestDaoFactory.class, loader = AnnotationConfigContextLoader.class)
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
    }

    @Test
    public void getAll() {
        dao.deleteAll();

        List<Member> members0 = dao.getAll();
        assertEquals(members0.size(), 0);

        dao.add(member1);
        List<Member> members1 = dao.getAll();
        assertEquals(members1.size(), 1);
        checkSameMember(member1, members1.get(0));

        dao.add(member2);
        List<Member> members2 = dao.getAll();
        assertEquals(members2.size(), 2);
        checkSameMember(member1, members2.get(0));
        checkSameMember(member2, members2.get(1));

        dao.add(member3);
        List<Member> members3 = dao.getAll();
        assertEquals(members3.size(), 3);
        checkSameMember(member1, members3.get(0));
        checkSameMember(member2, members3.get(1));
        checkSameMember(member3, members3.get(2));
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

    private void checkSameMember(Member member1, Member member2) {
        assertEquals(member1.getId(), member2.getId());
        assertEquals(member1.getName(), member2.getName());
        assertEquals(member1.getPassword(), member2.getPassword());
    }
}