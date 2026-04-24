package com.nfy.cancerapp.controller;

import com.nfy.cancerapp.model.Patient;
import com.nfy.cancerapp.service.HomeDashboardService;
import com.nfy.cancerapp.service.PatientService;
import com.nfy.cancerapp.service.QuickHomeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/**
 * 单页首页：当前阶段 + 今日风险提醒 + 三个快速入口（72203 Yang SiQing）。
 * 截图：/home（聚合读库，可作总览页）。
 */
@Controller
public class HomeController {

	private final PatientService patientService;
	private final QuickHomeService quickHomeService;
	private final HomeDashboardService homeDashboardService;

	public HomeController(PatientService patientService,
	                      QuickHomeService quickHomeService,
	                      HomeDashboardService homeDashboardService) {
		this.patientService = patientService;
		this.quickHomeService = quickHomeService;
		this.homeDashboardService = homeDashboardService;
	}

	@GetMapping("/home")
	public String home(HttpSession session, Model model) {
		String name = (String) session.getAttribute("userName");
		if (name == null || name.isBlank()) {
			return "redirect:/login";
		}
		String trimmed = name.trim();
		model.addAttribute("name", trimmed);
		Patient p = patientService.findByName(trimmed);
		if (p != null) {
			model.addAttribute("phaseStage", homeDashboardService.describeChemoStage(p.getId()));
			model.addAttribute("nutritionRiskHint", homeDashboardService.buildNutritionRiskHint(p.getId()));
		} else {
			model.addAttribute("phaseStage", "请先完成登录与患者建档。");
			model.addAttribute("nutritionRiskHint", "完善信息后可显示风险提醒。");
		}
		Integer cycle = quickHomeService.findCurrentCycleByPatientName(trimmed);
		model.addAttribute("currentCycleDisplay", cycle == null ? "—" : ("第 " + cycle + " 周期"));
		String todayMed = quickHomeService.findTodayMedicationTipByPatientName(trimmed, LocalDate.now());
		model.addAttribute("todayMedicationTip", todayMed == null || todayMed.isBlank() ? "暂无" : todayMed);
		return "home";
	}
}
