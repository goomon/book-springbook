package study.springbook.service;

import study.springbook.domain.Member;
import study.springbook.exception.TestMemberServiceException;

import java.util.List;

public class TestMemberServiceImpl extends MemberServiceImpl {
    private String id = "id4";

    @Override
    public List<Member> getAll() {
        for (Member member : super.getAll()) {
            if (member.getLevel().nextLevel() != null) {
                member.setLevel(member.getLevel().nextLevel());
                super.update(member);
            }
        }
        return null;
    }

    @Override
    protected void upgradeLevel(Member member) {
        if (member.getId().equals(id)) {
            throw new TestMemberServiceException();
        }
        super.upgradeLevel(member);
    }
}
