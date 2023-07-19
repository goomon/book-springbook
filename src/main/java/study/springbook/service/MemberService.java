package study.springbook.service;

import study.springbook.domain.Member;

import java.util.List;

public interface MemberService {

    void add(Member member);

    List<Member> getAll();

    void deleteAll();

    void update(Member member);

    void upgradeLevels();
}
