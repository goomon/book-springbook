package study.springbook.dao;

import study.springbook.domain.Member;

import java.sql.*;

public class MemberDao {

    private ConnectionMaker connectionMaker;

    public MemberDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void add(Member member) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = connectionMaker.makeConnection();

        PreparedStatement ps = c.prepareStatement("insert into member(id, name, password) values (?, ?, ?)");
        ps.setString(1, member.getId());
        ps.setString(2, member.getName());
        ps.setString(3, member.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public Member get(String id) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = connectionMaker.makeConnection();

        PreparedStatement ps = c.prepareStatement("select * from member where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        Member member = new Member();
        member.setId(rs.getString("id"));
        member.setName(rs.getString("name"));
        member.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return member;
    }
}
