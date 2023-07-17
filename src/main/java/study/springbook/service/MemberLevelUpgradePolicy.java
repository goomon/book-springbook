package study.springbook.service;

import study.springbook.domain.Member;

public interface MemberLevelUpgradePolicy {

    boolean canUpgradeLevel(Member member);

    void upgradeLevel(Member member);
}
