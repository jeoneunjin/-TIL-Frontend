package main.java.com.eunjin.model.dao;

import java.sql.Connection;

import com.ssafy.live.model.dto.Member;
import com.ssafy.live.model.dto.Page;
import com.ssafy.live.model.dto.SearchCondition;

/**
 * 회원 정보 관리를 위한 Data Access Object 인터페이스
 * 회원 정보의 추가, 조회, 초기화 기능을 정의합니다.
 */
public interface MemberDao {

    /**
     * 새로운 회원을 추가합니다.
     * 
     * @param con
     *            데이터베이스 연결 객체
     * @param member
     *            추가할 회원 정보
     * @return 추가된 회원 수 (성공 시 1, 실패 시 0)
     */
    int insert(Connection con, Member member);

    /**
     * 회원 정보를 초기화합니다.
     * 기존 데이터를 삭제하고 테스트용 데이터로 리셋합니다.
     */
    void reset(Connection con);

    public int getTotalCount(Connection con, SearchCondition condition);

    public Page<Member> search(Connection con, SearchCondition condition);
    
    public Member login(Connection con, String email, String password);
    
    public Member findByEmail(Connection con, String email);
    
    public Member memberModify(Connection con, Member member);
    
    public boolean memberDelete(Connection con, int mno);

}
