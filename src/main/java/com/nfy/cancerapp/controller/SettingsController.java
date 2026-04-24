package com.nfy.cancerapp.controller;

import com.nfy.cancerapp.model.Patient;
import com.nfy.cancerapp.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettingsController {

	private final PatientService patientService;

	public SettingsController(PatientService patientService) {
		this.patientService = patientService;
	}

	@GetMapping("/settings")
	public String page(HttpSession session, Model model) {
		String userName = (String) session.getAttribute("userName");
		model.addAttribute("loggedIn", userName != null && !userName.isBlank());
		model.addAttribute("userName", userName != null ? userName : "");
		if (userName != null && !userName.isBlank()) {
			Patient p = patientService.findByName(userName.trim());
			model.addAttribute("patient", p);
		}
		return "settings";
	}

	@PostMapping("/settings/logout")
	public String logout(HttpSession session, RedirectAttributes ra) {
		session.invalidate();
		ra.addFlashAttribute("notice", "您已退出登录");
		return "redirect:/login";
	}
}
