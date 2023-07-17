package study.springbook.service;

import study.springbook.domain.Member;

public interface MemberService {

    void add(Member member);

    void upgradeLevels();
}
