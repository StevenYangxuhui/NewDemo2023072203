package com.nfy.cancerapp.web;

import com.nfy.cancerapp.model.Patient;
import com.nfy.cancerapp.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
public class LoginPatient {

	private final PatientService patientService;

	public LoginPatient(PatientService patientService) {
		this.patientService = patientService;
	}

	/**
	 * @return 已登录患者；未登录时写入 flash error 并返回 null
	 */
	public Patient require(HttpSession session, RedirectAttributes ra) {
		String userName = (String) session.getAttribute("userName");
		if (userName == null || userName.isBlank()) {
			if (ra != null) {
				ra.addFlashAttribute("error", "请先登录");
			}
			return null;
		}
		Patient p = patientService.findByName(userName.trim());
		if (p == null) {
			if (ra != null) {
				ra.addFlashAttribute("error", "请先登录");
			}
			return null;
		}
		return p;
	}
}
