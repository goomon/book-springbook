package study.springbook.service;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import study.springbook.dao.MemberDao;
import study.springbook.domain.Level;
import study.springbook.domain.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

public class MemberService {

    private MemberDao memberDao;
    private DataSource dataSource;

    public static final int MIN_LOGIN_FOR_SILVER = 30;
    public static final int MIN_RECOMMEND_FOR_GOLD = 50;

    public void setMemberDao(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(Member member) {
        if (member.getLevel() == null) {
            member.setLevel(Level.BASIC);
        }
        memberDao.add(member);
    }

    public void upgradeLevels() throws Exception {
        TransactionSynchronizationManager.initSynchronization();
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

        try {
            List<Member> members = memberDao.getAll();
            for (Member member : members) {
                if (canUpgradeLevel(member)) {
                    upgradeLevel(member);
                }
            }
            c.commit();
        } catch (Exception e) {
            c.rollback();
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(c, dataSource);
            TransactionSynchronizationManager.unbindResource(dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    protected void upgradeLevel(Member member) {
        member.upgradeLevel();
        memberDao.update(member);
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
}
