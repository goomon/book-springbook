package study.springbook.factory;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import study.springbook.dao.JdbcContext;
import study.springbook.dao.MemberDao;
import study.springbook.dao.MemberDaoJdbc;
import study.springbook.service.*;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public MemberServiceImpl memberService() {
        MemberServiceImpl memberService = new MemberServiceImpl();
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

    @Bean
    public TransactionAdvice transactionAdvice() {
        TransactionAdvice transactionAdvice = new TransactionAdvice();
        transactionAdvice.setTransactionManager(transactionManager());
        return transactionAdvice;
    }

    @Bean
    public NameMatchClassMethodPointcut transactionPointcut() {
        NameMatchClassMethodPointcut pointcut = new NameMatchClassMethodPointcut();
        pointcut.setMappedClassName("*ServiceImpl");
        pointcut.setMappedName("upgrade*");
        return pointcut;
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setAdvice(transactionAdvice());
        advisor.setPointcut(transactionPointcut());
        return advisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }
}
