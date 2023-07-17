package study.springbook.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import study.springbook.domain.Level;
import study.springbook.domain.Member;
import study.springbook.exception.DuplicateMemberIdException;

import javax.sql.DataSource;
import java.util.List;

public class MemberDaoJdbc implements MemberDao {

    private JdbcTemplate jdbcTemplate;

    private RowMapper<Member> memberMapper = (rs, rowNum) -> {
        Member member = new Member();
        member.setId(rs.getString("id"));
        member.setName(rs.getString("name"));
        member.setPassword(rs.getString("password"));
        member.setLevel(Level.valueOf(rs.getInt("level")));
        member.setLogin(rs.getInt("login"));
        member.setRecommend(rs.getInt("recommend"));
        return member;
    };

    public MemberDaoJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void add(Member member) throws DuplicateMemberIdException {
        try {
            jdbcTemplate.update("insert into member(id, name, password, level, login, recommend) values (?, ?, ?, ?, ?, ?)",
                    member.getId(), member.getName(), member.getPassword(), member.getLevel().intValue(), member.getLogin(), member.getRecommend());
        } catch (DuplicateKeyException e) {
            throw new DuplicateMemberIdException(e);
        }
    }

    @Override
    public Member get(String id) {
        return jdbcTemplate.queryForObject("select * from member where id = ?",
                new Object[]{id}, memberMapper);
    }

    @Override
    public List<Member> getAll() {
        return jdbcTemplate.query("select * from member order by id", memberMapper);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from member");
    }

    @Override
    public int getCount() {
        return jdbcTemplate.queryForObject("select count(*) from member", Integer.class);
    }

    @Override
    public void update(Member member) {
        jdbcTemplate.update(
                "update member set name = ?, password = ?, level = ?, login = ?, recommend = ? where id = ?",
                member.getName(), member.getPassword(), member.getLevel().intValue(), member.getLogin(), member.getRecommend(), member.getId());
    }
}
