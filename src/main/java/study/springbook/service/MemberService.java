package study.springbook.service;

import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;

import java.util.List;

public class MemberService {

    private MemberDao memberDao;

    public void setMemberDao(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public void upgradeLevels() {
        List<Member> members = memberDao.getAll();
        for (Member member : members) {
            Boolean changed = null;
            if (member.getLevel() == Level.BASIC && member.getLogin() >= 50) {
                member.setLevel(Level.SILVER);
                changed = true;
            } else if (member.getLevel() == Level.SILVER && member.getRecommend() >= 30) {
                member.setLevel(Level.GOLD);
                changed = true;
            } else if (member.getLevel() == Level.GOLD) {
                changed = false;
            } else {
                changed = false;
            }
            if (changed) {
                memberDao.update(member);
            }
        }
    }
}
