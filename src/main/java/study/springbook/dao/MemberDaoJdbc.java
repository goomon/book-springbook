package study.springbook.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import study.springbook.domain.Level;
import study.springbook.domain.Member;
import study.springbook.exception.DuplicateMemberIdException;
import study.springbook.sqlservice.SqlService;

import javax.sql.DataSource;
import java.util.List;

public class MemberDaoJdbc implements MemberDao {

    private JdbcTemplate jdbcTemplate;
    private SqlService sqlService;

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

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    @Override
    public void add(Member member) throws DuplicateMemberIdException {
        try {
            jdbcTemplate.update(
                    sqlService.getSql("memberAdd"),
                    member.getId(),
                    member.getName(),
                    member.getPassword(),
                    member.getLevel().intValue(),
                    member.getLogin(),
                    member.getRecommend());
        } catch (DuplicateKeyException e) {
            throw new DuplicateMemberIdException(e);
        }
    }

    @Override
    public Member get(String id) {
        return jdbcTemplate.queryForObject(sqlService.getSql("memberGet"),
                new Object[]{id}, memberMapper);
    }

    @Override
    public List<Member> getAll() {
        return jdbcTemplate.query(sqlService.getSql("memberGetAll"), memberMapper);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update(sqlService.getSql("memberDeleteAll"));
    }

    @Override
    public int getCount() {
        return jdbcTemplate.queryForObject(sqlService.getSql("memberGetCount"), Integer.class);
    }

    @Override
    public void update(Member member) {
        jdbcTemplate.update(
                sqlService.getSql("memberUpdate"),
                member.getName(),
                member.getPassword(),
                member.getLevel().intValue(),
                member.getLogin(),
                member.getRecommend(),
                member.getId());
    }
}
