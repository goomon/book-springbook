package study.springbook;

import study.springbook.factory.DaoFactory;
import study.springbook.dao.MemberDao;
import study.springbook.domain.Member;

import java.sql.SQLException;

public class MemberDaoTest {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        MemberDao dao = new DaoFactory().memberDao();

        Member member = new Member();
        member.setId("id");
        member.setName("jaegoo");
        member.setPassword("password");

        dao.add(member);

        System.out.println(member.getId() + " successfully registered");

        Member member2 = dao.get(member.getId());
        System.out.println(member2.getName());
        System.out.println(member2.getPassword());

        System.out.println(member2.getId() + " successfully checked");
    }
}
