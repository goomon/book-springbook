package study.springbook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;
import study.springbook.factory.TestDaoFactory;

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
    public void upgradeLevels() {
        memberDao.deleteAll();
        for (Member member : members) {
            memberDao.add(member);
        }

        memberService.upgradeLevels();

        checkLevelUpgrade(members.get(0), false);
        checkLevelUpgrade(members.get(1), true);
        checkLevelUpgrade(members.get(2), false);
        checkLevelUpgrade(members.get(3), true);
        checkLevelUpgrade(members.get(4), false);
    }

    private void checkLevelUpgrade(Member member, boolean upgraded) {
        Member memberUpdate = memberDao.get(member.getId());
        if (upgraded) {
            assertEquals(member.getLevel().nextLevel(), memberUpdate.getLevel());
        } else {
            assertEquals(member.getLevel(), memberUpdate.getLevel());
        }
    }
}