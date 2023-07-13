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

        Member member = new Member();
        member.setId("id");
        member.setName("jaegoo");
        member.setPassword("password");

        dao.add(member);

        Member member2 = dao.get(member.getId());

        assertEquals(member2.getName(), member.getName());
        assertEquals(member2.getPassword(), member.getPassword());
    }
}