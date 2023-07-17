package study.springbook.dao;

import study.springbook.domain.Member;

import java.util.List;

public interface MemberDao {

    void add(Member member);

    Member get(String id);

    List<Member> getAll();

    void deleteAll();

    int getCount();
}
