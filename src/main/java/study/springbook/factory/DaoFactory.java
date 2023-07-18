package study.springbook.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import study.springbook.dao.JdbcContext;
import study.springbook.dao.MemberDao;
import study.springbook.dao.MemberDaoJdbc;
import study.springbook.service.MemberService;
import study.springbook.service.MemberServiceImpl;
import study.springbook.service.MemberServiceTx;
import study.springbook.service.TxProxyFactoryBean;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public TxProxyFactoryBean memberService() {
        TxProxyFactoryBean txProxyFactoryBean = new TxProxyFactoryBean();
        txProxyFactoryBean.setTarget(memberServiceImpl());
        txProxyFactoryBean.setTransactionManager(transactionManager());
        txProxyFactoryBean.setPattern("upgradeLevels");
        txProxyFactoryBean.setServiceInterface(MemberService.class);
        return txProxyFactoryBean;
    }

    @Bean
    public MemberServiceImpl memberServiceImpl() {
        MemberServiceImpl memberServiceImpl = new MemberServiceImpl();
        memberServiceImpl.setMemberDao(memberDao());
        return memberServiceImpl;
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
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
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
