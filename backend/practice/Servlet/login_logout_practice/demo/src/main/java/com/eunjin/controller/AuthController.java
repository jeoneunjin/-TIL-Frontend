package main.java.com.eunjin.controller;

import java.io.IOException;
import java.util.*;
import com.ssafy.live.model.dto.Member;
import com.ssafy.live.model.dto.Page;
import com.ssafy.live.model.dto.SearchCondition;
import com.ssafy.live.model.service.BasicMemberService;
import com.ssafy.live.model.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServelt("/auth")
@SuppressWarnings("serial")
public class AuthController {
    
    private final main.java.com.eunjin.model.service.MemberService mService = BasicMemberService.getService();
    
    @Override
    protected void doGet(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        String action = getActionParameter(request, response);
        switch(action){
            case "index" -> redirect(request, response, "/index.jsp");
            //회원목록조회
            case "member-list" -> memberList(request, response);
            //회원정보디테일
            case "member-detail" -> memberDetail(request, response);
            //회원정보수정폼
            case "member-modify-form" -> memberModifyForm(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    @Override
    protected void doPost(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        String action = getActionParameter(request, response);
        switch(action){
            case "index" -> redirect(request, response, "/index.jsp");
            //회원정보수정
            case "member-modify" -> memberModify(request, response);
            //회원 삭제
            case "member-delete" -> memberDelete(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    //회원목록조회
    private void memberList(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        String key = request.getParameter("key");
        String word = request.getParameter("word");

        int currentPage = 1;
        try {
            String currentPageStr = request.getParameter("currentPage");
            currentPage = Integer.parseInt(currentPageStr);
        } catch (Exception e) {
        }

        Page<Member> page = mService.search(new SearchCondition(key, word, currentPage));
        request.setAttribute("page", page);
        forward(request, response, "/member/member-list.jsp");
    }
    //회원정보디테일
    private void memberDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	String email = request.getParameter("email");
    	Member member = mService.findByEmail(email);
    	request.setAttribute("member", member);
    	forward(request, response, "/member/member-detail.jsp");
    }
    //회원정보수정폼
    //템플릿
    private void memberModifyForm(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        String email = request.getParameter("email");
        Member member = mService.findByEmail(email);
        request.setAttribute("member", member);
        forward(request, response, "/member/member-modify-form.jsp");
    }
    //회원정보수정
    private void memberModify(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String pass = request.getParameter("pass");
        String role = request.getParameter("role");

        int mno = Integer.parseInt(request.getParameter("mno"));

        Member member = new Member(mno, name, email, pass, role);
        member = mService.memberModify(member);

        HttpSession session = request.getSession();
        //현재 로그인 사용자 정보
        Member loginUser = (Member)session.getAttribute("loginUser");

        //현재 로그인 사용자와 수정된 사용자가 같을 때
        if(loginUser.getEmail().equals(email)){
            session.setAttribute("loginUser", member);
        }

        redirect(requset, response, "/auth?action-member-detail&email="+email);
    }

    //템플릿
    private void memberDelete(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        try {
            int mno = Integer.parseInt(request.getParameter("mno"));
            //회원 삭제
            mService.memberDelete(mno);
            //현재 로그인 사용자 정보
            HttpSession session = request.getSession();
            main.java.com.eunjin.model.dto.Member loginUser = session.getAttribute("loginUser");
            //현재 로그인 사용자와 삭제된 사용자가 같을 때
            if(loginUser != null && loginUser.getMno() == mno){
                //세션 초기화(로그아웃)
                session.invalidate(); 
                //리다이렉트
                redirect(request, response, "/");
            } else {
                redirect(request, response, "/auth?action=member-list&currentPage=1");
            }
        } catch (Exception e){
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/member/member-list.jsp");
        }
    }


    //템플릿
    private void template(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        
    }
    
    
}
