package study.springbook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;
import study.springbook.factory.TestDaoFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static study.springbook.service.MemberServiceImpl.*;

@SpringBootTest
@ContextConfiguration(classes = TestDaoFactory.class, loader = AnnotationConfigContextLoader.class)
class MemberServiceTest {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private MemberService memberService;
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
        MemberServiceImpl memberServiceImpl = new MemberServiceImpl();

        MockMemberDao mockMemberDao = new MockMemberDao(members);
        memberServiceImpl.setMemberDao(mockMemberDao);

        MockMailSender mockMailSender = new MockMailSender();
        memberServiceImpl.setMailSender(mockMailSender);

        memberServiceImpl.upgradeLevels();

        List<Member> updated = mockMemberDao.getUpdated();
        assertEquals(2, updated.size());
        checkMemberAndLevel(updated.get(0), "id2", Level.SILVER);
        checkMemberAndLevel(updated.get(1), "id4", Level.GOLD);

        List<String> requests = mockMailSender.getRequests();
        assertEquals(2, requests.size());
        assertEquals(members.get(1).getEmail(), requests.get(0));
        assertEquals(members.get(3).getEmail(), requests.get(1));
    }

    @Test
    public void mockUpgradeLevels() {
        MemberServiceImpl memberServiceImpl = new MemberServiceImpl();

        MemberDao mockMemberDao = mock(MemberDao.class);
        when(mockMemberDao.getAll()).thenReturn(members);
        memberServiceImpl.setMemberDao(mockMemberDao);

        MailSender mockMailSender = mock(MailSender.class);
        memberServiceImpl.setMailSender(mockMailSender);

        memberServiceImpl.upgradeLevels();

        verify(mockMemberDao, times(2)).update(any(Member.class));
        verify(mockMemberDao).update(members.get(1));
        assertEquals(Level.SILVER, members.get(1).getLevel());
        verify(mockMemberDao).update(members.get(3));
        assertEquals(Level.GOLD, members.get(3).getLevel());

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertEquals(members.get(1).getEmail(), mailMessages.get(0).getTo()[0]);
        assertEquals(members.get(3).getEmail(), mailMessages.get(1).getTo()[0]);
    }

    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() throws Exception {
        MemberServiceImpl testMemberService = new TestMemberService(members.get(3).getId());
        testMemberService.setMemberDao(memberDao);
        testMemberService.setMailSender(mailSender);

        ProxyFactoryBean proxyFactoryBean = context.getBean("&memberService", ProxyFactoryBean.class);
        proxyFactoryBean.setTarget(testMemberService);
        MemberService memberServiceTx = (MemberService) proxyFactoryBean.getObject();

        memberDao.deleteAll();
        for (Member member : members) {
            memberDao.add(member);
        }

        try {
            memberServiceTx.upgradeLevels();
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

    private void checkMemberAndLevel(Member updated, String expectedId, Level expectedLevel) {
        assertEquals(expectedId, updated.getId());
        assertEquals(expectedLevel, updated.getLevel());
    }

    static class TestMemberService extends MemberServiceImpl {
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

    static class MockMemberDao implements MemberDao {
        private List<Member> members;
        private List<Member> updated = new ArrayList<>();

        public MockMemberDao(List<Member> members) {
            this.members = members;
        }

        public List<Member> getUpdated() {
            return updated;
        }

        @Override
        public List<Member> getAll() {
            return members;
        }

        @Override
        public void update(Member member) {
            updated.add(member);
        }

        @Override
        public void add(Member member) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Member get(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }
    }
}