package study.springbook.factory;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import study.springbook.dao.JdbcContext;
import study.springbook.dao.MemberDao;
import study.springbook.dao.MemberDaoJdbc;
import study.springbook.service.*;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class TestDaoFactory {

    @Bean
    public MemberServiceImpl memberService() {
        MemberServiceImpl memberService = new MemberServiceImpl();
        memberService.setMemberDao(memberDao());
        memberService.setMailSender(mailSender());
        return memberService;
    }

    @Bean
    public TestMemberServiceImpl testMemberService() {
        TestMemberServiceImpl testMemberService = new TestMemberServiceImpl();
        testMemberService.setMemberDao(memberDao());
        testMemberService.setMailSender(mailSender());
        return testMemberService;
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
        dataSource.setUrl("jdbc:postgresql://localhost:5432/test_db");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");

        return dataSource;
    }

    @Bean
    public MailSender mailSender() {
        MailSender mailSender = new DummyMailSender();
        return mailSender;
    }

    @Bean
    public TransactionInterceptor transactionAdvice() {
        TransactionInterceptor transactionAdvice = new TransactionInterceptor();
        transactionAdvice.setTransactionManager(transactionManager());
        transactionAdvice.setTransactionAttributeSource(new AnnotationTransactionAttributeSource());
        return transactionAdvice;
    }

    @Bean
    public TransactionAttributeSourceAdvisor transactionAdvisor() {
        TransactionAttributeSourceAdvisor advisor = new TransactionAttributeSourceAdvisor();
        advisor.setTransactionInterceptor(transactionAdvice());
        return advisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }
}
