package study.springbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import study.springbook.service.DummyMailSender;
import study.springbook.service.MemberService;
import study.springbook.service.TestMemberServiceImpl;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"study.springbook.dao", "study.springbook.service", "study.springbook.sqlservice"})
@Import(SqlServiceContext.class)
@PropertySource("/database.properties")
public class AppContext {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        try {
            dataSource.setDriverClass((Class<? extends Driver>) Class.forName(env.getProperty("db.driverClass")));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));

        return dataSource;
    }

    @Bean
    public TransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Configuration
    @Profile("production")
    public static class ProductionAppContext {
        @Bean
        public MailSender mailSender() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("localhost");
            return mailSender;
        }
    }

    @Configuration
    @Profile("test")
    public static class TestAppContext {
        @Bean
        public MemberService testMemberService() {
            return new TestMemberServiceImpl();
        }

        @Bean
        public MailSender mailSender() {
            return new DummyMailSender();
        }
    }
}
