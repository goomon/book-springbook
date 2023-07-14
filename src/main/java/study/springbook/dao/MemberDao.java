package study.springbook.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import study.springbook.domain.Member;

import java.sql.*;

public class MemberDao {

    private JdbcContext jdbcContext;

    public void setJdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    public void add(Member member) throws SQLException {
        jdbcContext.executeSql(
                "insert into member(id, name, password) values (?, ?, ?)",
                member.getId(),
                member.getName(),
                member.getPassword()
        );
    }

    public Member get(String id) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = jdbcContext.getDataSource().getConnection();

        PreparedStatement ps = c.prepareStatement("select * from member where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        Member member = null;
        if (rs.next()) {
            member = new Member();
            member.setId(rs.getString("id"));
            member.setName(rs.getString("name"));
            member.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if (member == null) throw new EmptyResultDataAccessException(1);

        return member;
    }

    public void deleteAll() throws SQLException {
        jdbcContext.executeSql("delete from member");
    }

    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = jdbcContext.getDataSource().getConnection();
            ps = c.prepareStatement("select count(*) from member");

            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
