package com.ssafy.exam.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.ssafy.exam.model.dto.Member;
import com.ssafy.exam.model.service.BasicMobileService;
import com.ssafy.exam.model.service.MobileService;

/**
 * Servlet implementation class MemberController
 */
@WebServlet("/member")
public class MemberController extends HttpServlet implements ControllerHelper {
	private static final long serialVersionUID = 1L;
	private final MobileService service = BasicMobileService.getService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = getActionParameter(request, response);
		switch(action) {
		case "index" -> redirect(request, response, "/");
		case "login-form" -> forward(request, response, "/login-form.jsp");
		case "logout" -> doLogout(request, response);
		default -> response.sendError(HttpServletResponse.SC_NOT_FOUND); //404처리
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = getActionParameter(request, response);
		switch(action) {
		case "index" -> redirect(request, response, "/");
		case "login" -> doLogin(request, response);
		default -> response.sendError(HttpServletResponse.SC_NOT_FOUND); //404처리
		}
	}
	
	private void doLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String pass = request.getParameter("pass");
		String rememberMe = request.getParameter("remember-me");
		Member member = service.login(email, pass);
		if(member != null) {
			request.getSession().setAttribute("loginUser", member);
			if(rememberMe == null) {
				setupCookie("rememberMe", "bye", 0, null, response);
			} else {
				setupCookie("rememberMe", email, 60*60*24*365, null, response);
			}
			redirect(request, response, "/");
		} else {
			request.setAttribute("alertMsg", "로그인에 실패했습니다.");
			forward(request, response, "/member/login-form.jsp");
		}
	}

	private void doLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession().invalidate();
		redirect(request, response, "/");
	}

	
	private void template(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
