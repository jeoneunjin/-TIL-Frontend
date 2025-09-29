package main.java.com.eunjin.controller;

import java.io.IOException;
import java.util.Map;

import com.ssafy.live.model.dto.Member;
import com.ssafy.live.model.service.BasicMemberService;
import com.ssafy.live.model.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.*;

@WebServlet("/member")
public class MemberController extends HttpServlet implements ControllerHelper {
    
    private final MemberService mService = BasicMemberService.getService();

    @Override
    protected void doGet(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        Striing action = getActionParameter(request, response);
        switch (action) {
            case "index" -> redirect(requset, response, "/index.jsp");
            //회원가입
            case "member-regist-form" -> forward(request, response, "/member/member-regist-form.jsp");
            //회원목록조회
            case "member-list-regist" -> memberListReset(request, response);
            //로그인
            case "login-form" -> forward(request, response, "/member/login.jsp");
            //로그아웃
            case "logout" -> logout(request, response);
            //이메일중복확인
            case "email-check" -> emailCheck(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @override
    protected void doPost(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        String action = getActionParameter(request, response);
        switch(action){
            case "index" -> redirect(request, response, "/index.jsp");
            //로그인 폼
            case "login" -> login(request, response);
            //회원가입 폼
            case "member-reigst" -> memberRegist(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    //회원 가입
    private void memberRegist(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        try{
            String name = request.getParameter("name");
            String pass = request.getParameter("pass");
            String email = request.getParameter("email");

            Memeber member = new Member(name, email, pass);

            mService.registMember(member);

            // redirect하기 때문에 session에 저장 해야 한다. 
            String alertMsg = "등록되었습니다. 로그인 후 사용해주세요";
            HttpSession session = request.getSession();
            session.setAttribute("alertMsg", alertMsg);
            redirect(request, response, "/");
        } catch (Exception e){
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            forward(request, response, "/member/member-regist-fomr.jsp");
        }
    }
    
    private void memberListReset(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        mService.reset();
        redirect(request, response, "/auth?action=member-list&currentPage=1");
    }
    //로그인
    private void login(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        String email = request.getParameter("email");
        String pass = request.getParameter("pass");
        // 아이디 저장 여부
        String rememberMe = request.getParameter("remember-me");
        
        //기입한 이메일과 비번을 가지는 회원 찾아오기
        Member member = mService.login(email, pass);

        if(member != null){
            request.getSession.setAttribute("loginUser", member);
            if(rememberMe == null){
                setupCookie("rememberMe", "bye", 0, null, response);
            }
            else{
                setupCookie("rememberMe", email, 60 * 60 * 24 * 365, null, response);
            }
            redirect(request, response, "/");
        } else {
            reqeest.setAttribute("alertMsg", "로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요");
            forward(request, response, "/member/login-form.jsp");
        }
    }
    //로그아웃
    private void logout(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        request.getSession.invalidate();
        redirect(request, response, "/");
    }
    //이메일 중복 확인
    //email 중복 여부를 JSON으로 반환
    private void emailCheck(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        String email = request.getParameter("email");  
        Member member = mService.findByEmail(email);
        Map<String, Boolean> result = Map.of("canUse", member==null);
        toJSON(result, response);
    }
    //템플릿
    private void template(HttpServletReqeust request, HttpServletResponse response) throws ServletException, IOException{
        
    }
}
