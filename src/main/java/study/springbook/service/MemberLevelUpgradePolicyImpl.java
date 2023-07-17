package study.springbook.service;

import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;

import static study.springbook.service.MemberService.*;

public class MemberLevelUpgradePolicyImpl implements MemberLevelUpgradePolicy {

    private MemberDao memberDao;

    public MemberLevelUpgradePolicyImpl(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public boolean canUpgradeLevel(Member member) {
        Level level = member.getLevel();
        return switch (level) {
            case BASIC -> member.getLogin() >= MIN_LOGIN_FOR_SILVER;
            case SILVER -> member.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            case GOLD -> false;
            default -> throw new IllegalArgumentException("Unknown Level: " + level);
        };
    }

    @Override
    public void upgradeLevel(Member member) {
        member.upgradeLevel();
        memberDao.update(member);
    }
}
