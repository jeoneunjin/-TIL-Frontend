package main.java.com.eunjin.model.service;

import java.sql.Connection;
import java.util.List;

import com.ssafy.live.model.dao.FileMemberDao;
import com.ssafy.live.model.dao.MemberDao;
import com.ssafy.live.model.dto.Member;
import com.ssafy.live.model.dto.Page;
import com.ssafy.live.model.dto.SearchCondition;

public class BasicMemberService implements MemberService {
    private MemberDao dao = FileMemberDao.getInstance();

    private static BasicMemberService service = new BasicMemberService();

    private BasicMemberService() {
    }

    public static BasicMemberService getService() {
        return service;
    }

    @Override
    public int registMember(Member member) {
        return dao.insert(getConnection(), member);
    }

    @Override
    public void reset() {
        dao.reset(getConnection());
    }

    @Override
    public Page<Member> search(SearchCondition condition) {
        return dao.search(getConnection(), condition);
    }

    @Override
    public Member login(String email, String password) {
        return dao.login(getConnection(), email, password);
    }

    @Override
    public Member findByEmail(String email) {
        return dao.findByEmail(getConnection(), email);
    }

    @Override
    public Member memberModify(Member member) {
        return dao.memberModify(getConnection(), member);
    }
    
    @Override
    public boolean memberDelete(int mno) {
        return dao.memberDelete(getConnection(), mno);
    }

    private Connection getConnection() {
        // 지금은 null을 반환하지만 JDBC 후에 실제 연결을 반환하도록 구현 필요
        return null;
    }



}
