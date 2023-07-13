package study.springbook.dao;

import study.springbook.domain.Member;

import javax.sql.DataSource;
import java.sql.*;

public class MemberDao {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(Member member) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = dataSource.getConnection();

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
        Connection c = dataSource.getConnection();

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
