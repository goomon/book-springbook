package study.springbook.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;

import java.util.List;

@Service("memberService")
public class MemberServiceImpl implements MemberService {

    private MemberDao memberDao;
    private MailSender mailSender;

    public static final int MIN_LOGIN_FOR_SILVER = 30;
    public static final int MIN_RECOMMEND_FOR_GOLD = 50;

    @Autowired
    public void setMemberDao(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Autowired
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void add(Member member) {
        if (member.getLevel() == null) {
            member.setLevel(Level.BASIC);
        }
        memberDao.add(member);
    }

    @Override
    public Member get(String id) {
        return memberDao.get(id);
    }

    @Override
    public List<Member> getAll() {
        return memberDao.getAll();
    }

    @Override
    public void deleteAll() {
        memberDao.deleteAll();
    }

    @Override
    public void update(Member member) {
        memberDao.update(member);
    }

    @Override
    public void upgradeLevels() {
        List<Member> members = memberDao.getAll();
        for (Member member : members) {
            if (canUpgradeLevel(member)) {
                upgradeLevel(member);
            }
        }
    }

    protected void upgradeLevel(Member member) {
        member.upgradeLevel();
        memberDao.update(member);
        sendUpgradeEMail(member);
    }

    private boolean canUpgradeLevel(Member member) {
        Level level = member.getLevel();
        return switch (level) {
            case BASIC -> member.getLogin() >= MIN_LOGIN_FOR_SILVER;
            case SILVER -> member.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            case GOLD -> false;
            default -> throw new IllegalArgumentException("Unknown Level: " + level);
        };
    }

    private void sendUpgradeEMail(Member member) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(member.getEmail());
        mailMessage.setFrom("user@mail.org");
        mailMessage.setSubject("Upgrade notification");
        mailMessage.setText("Your grade is upgraded to " + member.getLevel().name() + " level.");

        mailSender.send(mailMessage);
    }
}
