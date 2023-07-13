package study.springbook.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import study.springbook.dao.ConnectionMaker;
import study.springbook.dao.MemberDao;
import study.springbook.dao.NConnectionMaker;

@Configuration
public class DaoFactory {

    @Bean
    public MemberDao memberDao() {
        return new MemberDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new NConnectionMaker();
    }
}
