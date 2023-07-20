package study.springbook.factory;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import study.springbook.dao.JdbcContext;
import study.springbook.dao.MemberDao;
import study.springbook.dao.MemberDaoJdbc;
import study.springbook.service.*;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DaoFactory {

    @Bean
    public MemberServiceImpl memberService() {
        MemberServiceImpl memberService = new MemberServiceImpl();
        memberService.setMemberDao(memberDao());
        return memberService;
    }

    @Bean
    public MemberDaoJdbc memberDao() {
        MemberDaoJdbc memberDao = new MemberDaoJdbc(dataSource());
        memberDao.setSqlService(sqlService());
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

    @Bean
    public SimpleSqlService sqlService() {
        SimpleSqlService sqlService = new SimpleSqlService();

        Map<String, String> map = new HashMap<>();
        map.put("memberAdd", "insert into member(id, name, password, level, login, recommend) values (?, ?, ?, ?, ?, ?)");
        map.put("memberGet", "select * from member where id = ?");
        map.put("memberGetAll", "select * from member order by id");
        map.put("memberDeleteAll", "delete from member");
        map.put("memberGetCount", "select count(*) from member");
        map.put("memberUpdate", "update member set name = ?, password = ?, level = ?, login = ?, recommend = ? where id = ?");

        sqlService.setSqlMap(map);

        return sqlService;
    }
}
