package com.ssafy.test.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionInterceptor implements HandlerInterceptor {
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("userInfo") != null) {
            return true;
        } else {
        	request.setAttribute("msg", "로그인 권한이 필요합니다." );
        	request.getRequestDispatcher("/user/login").forward(request, response);return false;
        }
    }
}
