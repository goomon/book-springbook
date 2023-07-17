package study.springbook.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import study.springbook.dao.JdbcContext;
import study.springbook.dao.MemberDao;
import study.springbook.dao.MemberDaoJdbc;
import study.springbook.service.MemberService;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public MemberService memberService() {
        MemberService memberService = new MemberService();
        memberService.setMemberDao(memberDao());
        return memberService;
    }

    @Bean
    public MemberDao memberDao() {
        MemberDao memberDao = new MemberDaoJdbc(dataSource());
        return memberDao;
    }

    @Bean
    public JdbcContext jdbcContext() {
        JdbcContext jdbcContext = new JdbcContext();
        jdbcContext.setDataSource(dataSource());
        return jdbcContext;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(org.postgresql.Driver.class);
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");

        return dataSource;
    }
}
