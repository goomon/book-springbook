package study.springbook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import study.springbook.domain.Level;
import study.springbook.domain.Member;
import study.springbook.exception.DuplicateMemberIdException;
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
        member1 = new Member("id1", "name1", "password1", Level.BASIC, 1, 0);
        member2 = new Member("id2", "name2", "password2", Level.SILVER, 55, 10);
        member3 = new Member("id3", "name3", "password3", Level.GOLD, 100,40);
    }

    @Test
    public void getAll() {
        dao.deleteAll();

        List<Member> members0 = dao.getAll();
        assertEquals(0, members0.size());

        dao.add(member1);
        List<Member> members1 = dao.getAll();
        assertEquals(1, members1.size());
        checkSameMember(member1, members1.get(0));

        dao.add(member2);
        List<Member> members2 = dao.getAll();
        assertEquals(2, members2.size());
        checkSameMember(member1, members2.get(0));
        checkSameMember(member2, members2.get(1));

        dao.add(member3);
        List<Member> members3 = dao.getAll();
        assertEquals(3, members3.size());
        checkSameMember(member1, members3.get(0));
        checkSameMember(member2, members3.get(1));
        checkSameMember(member3, members3.get(2));
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertEquals(0, dao.getCount());

        dao.add(member1);
        assertEquals(1, dao.getCount());

        dao.add(member2);
        assertEquals(2, dao.getCount());

        dao.add(member3);
        assertEquals(3, dao.getCount());

        Member data1 = dao.get(member1.getId());
        assertEquals(member1.getName(), data1.getName());
        assertEquals(member1.getPassword(), data1.getPassword());

        Member data2 = dao.get(member2.getId());
        assertEquals(member2.getName(), data2.getName());
        assertEquals(member2.getPassword(), data2.getPassword());
    }

    @Test
    public void getUserFailure() {
        dao.deleteAll();
        assertEquals(0, dao.getCount());

        assertThrows(EmptyResultDataAccessException.class, () -> {
            dao.get("unknown");
        });
    }

    @Test
    public void duplicateKey() {
        dao.deleteAll();
        assertEquals(0, dao.getCount());

        dao.add(member1);
        assertThrows(DuplicateMemberIdException.class, () -> {
            dao.add(member1);
        });
    }

    @Test
    public void update() {
        dao.deleteAll();

        dao.add(member1);
        dao.add(member2);

        member1.setName("member_1");
        member1.setPassword("password_1");
        member1.setLevel(Level.GOLD);
        member1.setLogin(1000);
        member1.setRecommend(999);

        dao.update(member1);

        Member member1Update = dao.get(member1.getId());
        checkSameMember(member1, member1Update);
        Member member2Same = dao.get(member2.getId());
        checkSameMember(member2, member2Same);
    }

    private void checkSameMember(Member member1, Member member2) {
        assertEquals(member2.getId(), member1.getId());
        assertEquals(member2.getName(), member1.getName());
        assertEquals(member2.getPassword(), member1.getPassword());
        assertEquals(member2.getLevel(), member1.getLevel());
        assertEquals(member2.getLogin(), member1.getLogin());
        assertEquals(member2.getRecommend(), member1.getRecommend());
    }
}