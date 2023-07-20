package study.springbook.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import study.springbook.domain.Level;
import study.springbook.domain.Member;
import study.springbook.exception.DuplicateMemberIdException;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(locations = "/test-applicationContext.xml")
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
        assertThat(members0.size()).isEqualTo(0);

        dao.add(member1);
        List<Member> members1 = dao.getAll();
        assertThat(members1.size()).isEqualTo(1);
        checkSameMember(member1, members1.get(0));

        dao.add(member2);
        List<Member> members2 = dao.getAll();
        assertThat(members2.size()).isEqualTo(2);
        checkSameMember(member1, members2.get(0));
        checkSameMember(member2, members2.get(1));

        dao.add(member3);
        List<Member> members3 = dao.getAll();
        assertThat(members3.size()).isEqualTo(3);
        checkSameMember(member1, members3.get(0));
        checkSameMember(member2, members3.get(1));
        checkSameMember(member3, members3.get(2));
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(member1);
        assertThat(dao.getCount()).isEqualTo(1);

        dao.add(member2);
        assertThat(dao.getCount()).isEqualTo(2);

        dao.add(member3);
        assertThat(dao.getCount()).isEqualTo(3);

        Member data1 = dao.get(member1.getId());
        assertThat(data1.getName()).isEqualTo(member1.getName());
        assertThat(data1.getPassword()).isEqualTo(member1.getPassword());

        Member data2 = dao.get(member2.getId());
        assertThat(data2.getName()).isEqualTo(member2.getName());
        assertThat(data2.getPassword()).isEqualTo(member2.getPassword());
    }

    @Test
    public void getUserFailure() {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        assertThatThrownBy(() -> dao.get("unknown")).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    public void duplicateKey() {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(member1);
        assertThatThrownBy(() -> dao.add(member1)).isInstanceOf(DuplicateMemberIdException.class);
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

    @Test
    @Transactional(readOnly = true)
    public void transactionSync() {
        assertThatThrownBy(() -> dao.deleteAll()).isInstanceOf(NonTransientDataAccessException.class);
    }

    private void checkSameMember(Member member1, Member member2) {
        assertThat(member1.getId()).isEqualTo(member2.getId());
        assertThat(member1.getName()).isEqualTo(member2.getName());
        assertThat(member1.getPassword()).isEqualTo(member2.getPassword());
        assertThat(member1.getLevel()).isEqualTo(member2.getLevel());
        assertThat(member1.getLogin()).isEqualTo(member2.getLogin());
        assertThat(member1.getRecommend()).isEqualTo(member2.getRecommend());
    }
}