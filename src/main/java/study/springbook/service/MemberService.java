package study.springbook.service;

import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;

import java.util.List;

public class MemberService {

    private MemberDao memberDao;
    private MemberLevelUpgradePolicy memberLevelUpgradePolicy;

    public static final int MIN_LOGIN_FOR_SILVER = 30;
    public static final int MIN_RECOMMEND_FOR_GOLD = 50;

    public void setMemberDao(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public void setMemberLevelUpgradePolicy(MemberLevelUpgradePolicy memberLevelUpgradePolicy) {
        this.memberLevelUpgradePolicy = memberLevelUpgradePolicy;
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
            if (memberLevelUpgradePolicy.canUpgradeLevel(member)) {
                memberLevelUpgradePolicy.upgradeLevel(member);
            }
        }
    }
}
