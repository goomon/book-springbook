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
                new Member("id1", "member1", "password1", Level.BASIC, 49, 0),
                new Member("id2", "member2", "password2", Level.BASIC, 50, 0),
                new Member("id3", "member3", "password3", Level.SILVER, 60, 29),
                new Member("id4", "member4", "password4", Level.SILVER, 60, 30),
                new Member("id5", "member5", "password5", Level.GOLD, 100, 100)
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

        checkLevel(members.get(0), Level.BASIC);
        checkLevel(members.get(1), Level.SILVER);
        checkLevel(members.get(2), Level.SILVER);
        checkLevel(members.get(3), Level.GOLD);
        checkLevel(members.get(4), Level.GOLD);
    }

    private void checkLevel(Member member, Level expected) {
        Member memberUpdate = memberDao.get(member.getId());
        assertEquals(expected, memberUpdate.getLevel());
    }
}