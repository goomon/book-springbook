package study.springbook.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import study.springbook.domain.Member;

import javax.sql.DataSource;
import java.util.List;

public class MemberDao {

    private JdbcTemplate jdbcTemplate;

    private RowMapper<Member> memberMapper = (rs, rowNum) -> {
        Member member = new Member();
        member.setId(rs.getString("id"));
        member.setName(rs.getString("name"));
        member.setPassword(rs.getString("password"));
        return member;
    };

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(Member member) {
        jdbcTemplate.update("insert into member(id, name, password) values (?, ?, ?)",
                member.getId(), member.getName(), member.getPassword());
    }

    public Member get(String id) {
        return jdbcTemplate.queryForObject("select * from member where id = ?",
                new Object[]{id}, memberMapper);
    }

    public List<Member> getAll() {
        return jdbcTemplate.query("select * from member order by id", memberMapper);
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from member");
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("select count(*) from member", Integer.class);
    }
}
