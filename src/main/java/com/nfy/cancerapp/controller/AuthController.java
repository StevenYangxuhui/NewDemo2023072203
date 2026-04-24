package com.nfy.cancerapp.controller;

import com.nfy.cancerapp.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 登录与会话：写入 session.userName 并 ensureExists 患者记录（72203）。
 * 截图：/login、登录后跳转 /home；页面底部可见学号标识。
 */
@Controller
public class AuthController {

	private final PatientService patientService;

	public AuthController(PatientService patientService) {
		this.patientService = patientService;
	}

	@GetMapping("/")
	public String welcome() {
		return "welcome";
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam("name") String name,
	                      @RequestParam("password") String password,
	                      HttpSession session,
	                      RedirectAttributes redirectAttributes) {
		if (name == null || name.isBlank()) {
			redirectAttributes.addFlashAttribute("error", "请输入姓名");
			return "redirect:/login";
		}
		String trimmed = name.trim();
		session.setAttribute("userName", trimmed);
		patientService.ensureExists(trimmed);
		return "redirect:/home";
	}

	@GetMapping("/quick-home")
	public String quickHomeLegacy() {
		return "redirect:/home";
	}
}
