package study.springbook.dao;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import study.springbook.domain.Member;
import study.springbook.factory.DaoFactory;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MemberDaoTest {

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        MemberDao dao = context.getBean("memberDao", MemberDao.class);
        Member member1 = new Member("id1", "name1", "password1");
        Member member2 = new Member("id2", "name2", "password2");
        Member member3 = new Member("id3", "name3", "password3");

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
}