package study.springbook;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import study.springbook.service.DummyMailSender;
import study.springbook.service.MemberService;
import study.springbook.service.TestMemberServiceImpl;

@Configuration
public class TestAppContext {

    @Bean
    public MemberService testMemberService() {
        return new TestMemberServiceImpl();
    }

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }
}
