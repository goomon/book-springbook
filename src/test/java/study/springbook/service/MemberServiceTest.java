package study.springbook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;
import study.springbook.factory.TestDaoFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static study.springbook.service.MemberService.*;

@SpringBootTest
@ContextConfiguration(classes = TestDaoFactory.class, loader = AnnotationConfigContextLoader.class)
class MemberServiceTest {

    @Autowired
    private MemberDao memberDao;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private MailSender mailSender;
    private List<Member> members;

    @BeforeEach
    public void setUp() {
        members = Arrays.asList(
                new Member("id1", "member1", "password1", Level.BASIC, MIN_LOGIN_FOR_SILVER - 1, 0),
                new Member("id2", "member2", "password2", Level.BASIC, MIN_LOGIN_FOR_SILVER, 0),
                new Member("id3", "member3", "password3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
                new Member("id4", "member4", "password4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
                new Member("id5", "member5", "password5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void add() {
        memberDao.deleteAll();

        Member memberWithLevel = members.get(4);
        Member memberWithoutLevel = members.get(0);
        memberWithoutLevel.setLevel(null);

        memberService.add(memberWithLevel);
        memberService.add(memberWithoutLevel);

        Member memberWithLevelRead = memberDao.get(memberWithLevel.getId());
        Member memberWithoutLevelRead = memberDao.get(memberWithoutLevel.getId());

        assertEquals(memberWithLevel.getLevel(), memberWithLevelRead.getLevel());
        assertEquals(Level.BASIC, memberWithoutLevelRead.getLevel());
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() {
        memberDao.deleteAll();
        for (Member member : members) {
            memberDao.add(member);
        }

        MockMailSender mockMailSender = new MockMailSender();
        memberService.setMailSender(mockMailSender);

        memberService.upgradeLevels();

        checkLevelUpgrade(members.get(0), false);
        checkLevelUpgrade(members.get(1), true);
        checkLevelUpgrade(members.get(2), false);
        checkLevelUpgrade(members.get(3), true);
        checkLevelUpgrade(members.get(4), false);

        List<String> requests = mockMailSender.getRequests();
        assertEquals(2, requests.size());
        assertEquals(members.get(1).getEmail(), requests.get(0));
        assertEquals(members.get(3).getEmail(), requests.get(1));
    }

    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() {
        MemberService testMemberService = new TestMemberService(members.get(3).getId());
        testMemberService.setMemberDao(memberDao);
        testMemberService.setMailSender(mailSender);
        testMemberService.setTransactionManager(transactionManager);

        memberDao.deleteAll();
        for (Member member : members) {
            memberDao.add(member);
        }

        try {
            testMemberService.upgradeLevels();
            fail("TestMemberServiceException expected");
        } catch (TestMemberServiceException e) {

        }

        checkLevelUpgrade(members.get(1), false);
    }

    private void checkLevelUpgrade(Member member, boolean upgraded) {
        Member memberUpdate = memberDao.get(member.getId());
        if (upgraded) {
            assertEquals(member.getLevel().nextLevel(), memberUpdate.getLevel());
        } else {
            assertEquals(member.getLevel(), memberUpdate.getLevel());
        }
    }

    static class TestMemberService extends MemberService {
        private String id;

        public TestMemberService(String id) {
            this.id = id;
        }

        @Override
        protected void upgradeLevel(Member member) {
            if (member.getId().equals(id)) {
                throw new TestMemberServiceException();
            }
            super.upgradeLevel(member);
        }
    }

    static class TestMemberServiceException extends RuntimeException {
    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... mailMessage) throws MailException {

        }
    }
}