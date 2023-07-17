package study.springbook.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

public class MemberService {

    private MemberDao memberDao;
    private DataSource dataSource;
    private PlatformTransactionManager transactionManager;

    public static final int MIN_LOGIN_FOR_SILVER = 30;
    public static final int MIN_RECOMMEND_FOR_GOLD = 50;

    public void setMemberDao(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void add(Member member) {
        if (member.getLevel() == null) {
            member.setLevel(Level.BASIC);
        }
        memberDao.add(member);
    }

    public void upgradeLevels() {
        // Transaction starts.
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            List<Member> members = memberDao.getAll();
            for (Member member : members) {
                if (canUpgradeLevel(member)) {
                    upgradeLevel(member);
                }
            }
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
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
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.org");
        Session s = Session.getInstance(props, null);

        MimeMessage message = new MimeMessage(s);
        try {
            message.setFrom(new InternetAddress("user@mail.org"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(member.getEmail()));
            message.setSubject("Upgrade notification");
            message.setText("Your grade is upgraded to " + member.getLevel().name() + " level.");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
