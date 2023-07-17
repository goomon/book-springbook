package study.springbook.service;

import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;

import java.util.List;

public class MemberService {

    private MemberDao memberDao;

    public static final int MIN_LOGIN_FOR_SILVER = 30;
    public static final int MIN_RECOMMEND_FOR_GOLD = 50;

    public void setMemberDao(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public void add(Member member) {
        if (member.getLevel() == null) {
            member.setLevel(Level.BASIC);
        }
        memberDao.add(member);
    }

    public void upgradeLevels() {
        List<Member> members = memberDao.getAll();
        for (Member member : members) {
            if (canUpgradeLevel(member)) {
                upgradeLevel(member);
            }
        }
    }

    private void upgradeLevel(Member member) {
        member.upgradeLevel();
        memberDao.update(member);
    }

    private boolean canUpgradeLevel(Member member) {
        Level level = member.getLevel();
        return switch (level) {
            case BASIC -> member.getLogin() >= MIN_LOGIN_FOR_SILVER;
            case SILVER -> member.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            case GOLD -> false;
            default -> throw new IllegalArgumentException("Unknown Level: " + level);
        };
    }
}
