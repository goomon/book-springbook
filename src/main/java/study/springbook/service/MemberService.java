package study.springbook.service;

import org.springframework.transaction.annotation.Transactional;
import study.springbook.domain.Member;

import java.util.List;

@Transactional
public interface MemberService {

    void add(Member member);

    @Transactional(readOnly = true)
    Member get(String id);

    @Transactional(readOnly = true)
    List<Member> getAll();

    void deleteAll();

    void update(Member member);

    void upgradeLevels();
}
