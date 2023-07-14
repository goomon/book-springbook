package study.springbook.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import study.springbook.domain.Member;
import study.springbook.statement.AddStatement;
import study.springbook.statement.DeleteAllStatement;
import study.springbook.statement.StatementStrategy;

import javax.sql.DataSource;
import java.sql.*;

public class MemberDao {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(Member member) throws SQLException {
        StatementStrategy st = new AddStatement(member);
        jdbcContextWithStatementStrategy(st);
    }

    public Member get(String id) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = dataSource.getConnection();

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
        StatementStrategy st = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(st);
    }

    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
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

    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
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
