package com.ssafy.test.controller;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import com.ssafy.test.model.dto.User;
import com.ssafy.test.model.service.UserService;

@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
	private static final long serialVersionUID = 1L;

	private final UserService userService;

	private UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/login")
	private String login(HttpSession session) {

		return "views/user/loginUser";
	}

	@PostMapping("/login")
	private String login(@ModelAttribute User user, @RequestParam(required = false) String remember,
			HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		try {
			User userInfo = userService.login(user);

			if (userInfo != null) {
				session.setAttribute("userInfo", userInfo);

				if (remember != null) {
					Cookie cookie = new Cookie("savedId", user.getId());
					cookie.setPath(request.getContextPath());
					cookie.setMaxAge(60 * 60 * 24 * 365);
				} else {
					Cookie cookie = new Cookie("savedId", user.getId());
					cookie.setPath(request.getContextPath());
					cookie.setMaxAge(0);
				}

				return "redirect:/";
			} else {
				model.addAttribute("msg", "로그인 실패");
				return "/user/loginUser";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.toString());
			return "/error/error";
		}
	}

	@GetMapping("/update")
	private String update(HttpSession session, Model model) {
		User user = (User) session.getAttribute("userInfo");
		try {
			model.addAttribute("user", userService.detail(user.getId()));
			return "/user/updateUser";
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.toString());
			return "/error/error";
		}

	}

	@GetMapping("/regist")
	private String regist() {
		return "/user/registUser";
	}

	@GetMapping("/detail")
	private String detail(HttpSession session, Model model) {
		try {
			User userInfo = (User) session.getAttribute("userInfo");
			User user = userService.detail(userInfo.getId());
			if (user != null) {
				model.addAttribute("user", user);
			} else {
				throw new NullPointerException();
			}
			return "/user";
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
			log.debug(e.toString());
			return "/error/error";
		}
	}

	@GetMapping("/logout")
	private String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@PostMapping("/regist")
	private String register(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
		try {
			int cnt = userService.register(user);

			if (cnt != 0) {
				redirectAttributes.addFlashAttribute("msg", "회원가입 성공. 로그인 해주세요.");
				return "redirect:/user/login";
			} else {
				redirectAttributes.addFlashAttribute("msg", "회원가입에 실패하였습니다.다시 시도해주세요.");
				return "redirect:/user/regist";
			}

		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.toString());
			return "/error/error";
		}

	}

	@PostMapping("/update")
	private String update(@ModelAttribute User user, Model model) {
		try {
			int cnt = userService.update(user);

			if (cnt != 0) {
				return "redirect:/user/detail";
			} else {
				model.addAttribute("msg", "회원 정보 수정에 실패하였습니다. 다시 시도해주세요.");
				return "/user/update";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.toString());
			return "/error/error";
		}
	}
}
