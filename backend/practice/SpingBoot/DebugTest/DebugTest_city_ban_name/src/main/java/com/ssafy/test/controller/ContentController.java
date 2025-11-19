package com.ssafy.test.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import com.ssafy.test.model.dto.Content;
import com.ssafy.test.model.dto.Genre;
import com.ssafy.test.model.dto.User;
import com.ssafy.test.model.service.ContentService;
import com.ssafy.test.model.service.ContentServiceImpl;

@Controller
@RequestMapping({ "/", "/content" })
@Slf4j
public class ContentController{

	private static final long serialVersionUID = 1L;

	private final ContentService contentService;

	public ContentController(ContentService contentService) {
		this.contentService = contentService;
	}
	
	@GetMapping
	private String main() {
		return "/main";
	}

	@GetMapping("/regist")
	private String goRegistPage(Model model) throws SQLException, ServletException, IOException {
		List<Genre> genreList = contentService.selectAllGenres();
		model.addAttribute("genreList", genreList);
		return "/content/registContent";
	}
	
	@PostMapping("/regist")
	private String regist(@ModelAttribute Content content, HttpSession session, Model model) {
		try {
			User user = (User) session.getAttribute("userInfo");
			content.setUserId(user.getId());
			if(content.getCode().equals("")||content.getTitle().equals(""))
				throw new NullPointerException();
			int cnt = contentService.insert(content);

			if (cnt == 1) {
				return "redirect:/content/list";
			} else {
				return "redirect:/content/regist";
			}
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
			log.debug(e.toString());
			model.addAttribute("msg", "무언가 잘못되었습니다.");
			return "/error/error";
		}

	}


	@GetMapping("/list")
	private String list(Model model) {
		try {
			List<Content> contentList = contentService.selectAll();
			if (contentList != null && contentList.size() != 0) {
				model.addAttribute("contentList", contentList);
				return "/content/listContent";
			} else {
				model.addAttribute("msg", "음원 목록 조회에 실패하였습니다.");
				return "/error/error";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.toString());
			model.addAttribute("msg", "무언가 잘못되었습니다.");
			return "/error/error";
		}

	}

	@GetMapping("/detail")
	private String detail(@RequestParam String code, Model model) {
		try {
			Content content = contentService.selectByCode(code);

			if (content == null || content.getCode() == null || content.getAlbum() == null
					|| content.getArtist() == null) {
				model.addAttribute("msg", "상세 정보 불러오기에 실패하였습니다.");
				return "/error/error";
			} else {
				model.addAttribute("content", content);
				return "/content/detailContent";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.toString());
			model.addAttribute("msg", "무언가 잘못되었습니다.");
			return "/error/error";
		}
	}

	@GetMapping("/update")
	private String goUpdatePage(@RequestParam String code, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		try {
			Content content = contentService.selectByCode(code);
			List<Genre> genreList = contentService.selectAllGenres();
			model.addAttribute("genreList", genreList);
			if (validateUser(session, code)) {
				if (content != null) {
					model.addAttribute("content", content);
					return "/content/updateContent";
				} else {
					redirectAttributes.addFlashAttribute("msg", "음원 정보 불러오기 실패");
					return "redirect:/content/list";
				}
			} else {
				redirectAttributes.addFlashAttribute("msg", "동록한 사용자만 수정할 수 있습니다.");
				return "redirect:/content/list";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.toString());
			model.addAttribute("msg", "무언가 잘못되었습니다.");
			return "/error/error";
		}
	}
	

	@PostMapping("/update")
	private String update(@ModelAttribute Content content, Model model, RedirectAttributes redirectAttributes) {
		try {
			int cnt = contentService.update(content);

			if (cnt == 1) {
				redirectAttributes.addFlashAttribute("msg", "음원 정보 수정 성공");
			} else {
				redirectAttributes.addFlashAttribute("msg", "음원 정보 수정 실패");
			}
			return "redirect:/content/list";
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.toString());
			model.addAttribute("msg", "무언가 잘못되었습니다.");
			return "/error/error";
		}
	}

	@GetMapping("delete")
	private String delete(String code, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
		try {
			if (validateUser(session, code)) {
				int cnt = contentService.delete(code);
				if (cnt == 1)					
					redirectAttributes.addFlashAttribute("msg", "음원 정보 삭제 성공");
				else
					redirectAttributes.addFlashAttribute("msg", "음원 정보 삭제 실패");
				return "redirect:/content/list";

			} else {
				redirectAttributes.addFlashAttribute("msg", "등록한 사용자만 삭제할 수 있습니다.");
				return "redirect:/content/list";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.toString());
			model.addAttribute("msg", "무언가 잘못되었습니다.");
			return "/error/error";
		}

	}

	private boolean validateUser(HttpSession session, String code) throws SQLException {
		User loginUser = (User) session.getAttribute("userInfo");
		Content content = contentService.selectByCode(code);
		if (loginUser != null && loginUser.getId().equals(content.getUserId()))
			return true;
		return false;
	}

}
