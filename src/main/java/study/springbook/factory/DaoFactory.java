package study.springbook.factory;

import study.springbook.dao.ConnectionMaker;
import study.springbook.dao.MemberDao;
import study.springbook.dao.NConnectionMaker;

public class DaoFactory {

    public MemberDao memberDao() {
        return new MemberDao(connectionMaker());
    }

    public ConnectionMaker connectionMaker() {
        return new NConnectionMaker();
    }
}
