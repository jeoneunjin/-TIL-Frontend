package com.ssafy.exam.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.ssafy.exam.controller.ControllerHelper;

/**
 * Servlet implementation class SessionFilter
 */
@WebFilter({"/auth"})
public class SessionFilter extends HttpServlet implements Filter, ControllerHelper{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		if(session.getAttribute("loginUser")!=null) {
			chain.doFilter(request, response);
		} else {
			session.setAttribute("alertMsg", "로그인 후 사용하세요.");
			redirect(req, res, "/member?action=login-form");
		}
		
	}



}
