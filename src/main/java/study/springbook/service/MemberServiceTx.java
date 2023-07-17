package study.springbook.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import study.springbook.domain.Member;

public class MemberServiceTx implements MemberService {

    private MemberService memberService;
    private PlatformTransactionManager transactionManager;

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void add(Member member) {
        memberService.add(member);
    }

    @Override
    public void upgradeLevels() {
        // Transaction starts.
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            memberService.upgradeLevels();
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
