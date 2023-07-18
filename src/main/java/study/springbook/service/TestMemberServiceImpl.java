package study.springbook.service;

import study.springbook.domain.Member;

public class TestMemberServiceImpl extends MemberServiceImpl {
    private String id = "id4";

    @Override
    protected void upgradeLevel(Member member) {
        if (member.getId().equals(id)) {
            throw new TestMemberServiceException();
        }
        super.upgradeLevel(member);
    }
}
