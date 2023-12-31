package study.springbook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import study.springbook.service.DummyMailSender;
import study.springbook.service.MemberService;
import study.springbook.service.TestMemberServiceImpl;
import study.springbook.sqlservice.SqlMapConfig;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"study.springbook.dao", "study.springbook.service", "study.springbook.sqlservice"})
@EnableSqlService
@PropertySource("/database.properties")
public class AppContext implements SqlMapConfig {

    @Value("${db.driverClass}")
    Class<? extends Driver> driverClass;
    @Value("${db.url}")
    String url;
    @Value("${db.username}")
    String username;
    @Value("${db.password}")
    String password;

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    public TransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Override
    public Resource getSqlMapResource() {
        return new ClassPathResource("/sql/sqlmap.xml");
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
