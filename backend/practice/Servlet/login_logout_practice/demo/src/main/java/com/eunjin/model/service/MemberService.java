package main.java.com.eunjin.model.service;

import com.ssafy.live.model.dto.Member;
import com.ssafy.live.model.dto.Page;
import com.ssafy.live.model.dto.SearchCondition;

public interface MemberService {

    int registMember(Member member) ;

    
    void reset() ;
    
    Page<Member> search(SearchCondition condition) ;

    Member login(String email, String password);
    
    Member findByEmail(String email);
    
    Member memberModify(Member member) ;
    
    boolean memberDelete(int mno) ;
}

