package study.springbook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;
import study.springbook.factory.TestDaoFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static study.springbook.service.MemberServiceImpl.*;

@SpringBootTest
@ContextConfiguration(classes = TestDaoFactory.class, loader = AnnotationConfigContextLoader.class)
class MemberServiceTest {

    @Autowired
    private MemberDao memberDao;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberService testMemberService;
    @Autowired
    private PlatformTransactionManager transactionManager;
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

        assertThat(memberWithLevelRead.getLevel()).isEqualTo(memberWithLevel.getLevel());
        assertThat(memberWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
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
        assertThat(updated.size()).isEqualTo(2);
        checkMemberAndLevel(updated.get(0), "id2", Level.SILVER);
        checkMemberAndLevel(updated.get(1), "id4", Level.GOLD);

        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(members.get(1).getEmail());
        assertThat(requests.get(1)).isEqualTo(members.get(3).getEmail());
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
        assertThat(members.get(1).getLevel()).isEqualTo(Level.SILVER);
        verify(mockMemberDao).update(members.get(3));
        assertThat(members.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(members.get(1).getEmail());
        assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(members.get(3).getEmail());
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
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

    @Test
    public void advisorAutoProxyCreator() {
        assertThat(testMemberService).isInstanceOf(Proxy.class);
    }


    @Test
    public void readOnlyTransactionAttribute() {
        memberDao.deleteAll();
        for (Member member : members) {
            memberDao.add(member);
        }

        assertThatThrownBy(() -> testMemberService.getAll()).isInstanceOf(NonTransientDataAccessException.class);
    }

    @Test
    public void transactionSync() {
        memberDao.deleteAll();
        assertThat(memberDao.getCount()).isEqualTo(0);

        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(txDefinition);

        try {
            memberService.add(members.get(0));
            memberService.add(members.get(1));
            assertThat(memberDao.getCount()).isEqualTo(2);
        } finally {
            transactionManager.rollback(status);
        }

        assertThat(memberDao.getCount()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void rollbackTest() {
        memberDao.deleteAll();
        assertThat(memberDao.getCount()).isEqualTo(0);

        for (Member member : members) {
            memberService.add(member);
        }
    }

    private void checkLevelUpgrade(Member member, boolean upgraded) {
        Member memberUpdate = memberDao.get(member.getId());
        if (upgraded) {
            assertThat(memberUpdate.getLevel()).isEqualTo(member.getLevel().nextLevel());
        } else {
            assertThat(memberUpdate.getLevel()).isEqualTo(member.getLevel());
        }
    }

    private void checkMemberAndLevel(Member updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId()).isEqualTo(expectedId);
        assertThat(updated.getLevel()).isEqualTo(expectedLevel);
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