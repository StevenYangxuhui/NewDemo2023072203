package com.nfy.cancerapp.controller;

import com.nfy.cancerapp.model.Patient;
import com.nfy.cancerapp.model.SupplementRule;
import com.nfy.cancerapp.service.RegimenService;
import com.nfy.cancerapp.service.SupplementRuleService;
import com.nfy.cancerapp.web.LoginPatient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 综合展示：当前方案参考药名 + 与这些药相关的规则摘要 + 少量通用慎用提醒。
 * 报告可并入功能模块6的补充剂规则展示；截图位 /interaction（72203）。
 */
@Controller
public class InteractionController {

	private final LoginPatient loginPatient;
	private final RegimenService regimenService;
	private final SupplementRuleService supplementRuleService;

	public InteractionController(LoginPatient loginPatient,
	                             RegimenService regimenService,
	                             SupplementRuleService supplementRuleService) {
		this.loginPatient = loginPatient;
		this.regimenService = regimenService;
		this.supplementRuleService = supplementRuleService;
	}

	@GetMapping("/interaction")
	public String page(HttpSession session, Model model, RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		List<String> drugs = regimenService.listDistinctDrugNamesForPatient(patient.getId());
		List<SupplementRule> matched = supplementRuleService.matchRulesForPatientDrugs(drugs);
		List<SupplementRule> general = supplementRuleService.briefGeneralTips(8);
		model.addAttribute("patient", patient);
		model.addAttribute("drugs", drugs);
		model.addAttribute("rulesAvoid", filterRisk(matched, "AVOID"));
		model.addAttribute("rulesCaution", filterRisk(matched, "CAUTION"));
		model.addAttribute("rulesSafe", filterRisk(matched, "SAFE"));
		model.addAttribute("generalTips", general);
		return "interaction";
	}

	private static List<SupplementRule> filterRisk(List<SupplementRule> all, String risk) {
		List<SupplementRule> out = new ArrayList<>();
		for (SupplementRule r : all) {
			if (r.getRiskLevel() != null && risk.equalsIgnoreCase(r.getRiskLevel())) {
				out.add(r);
			}
		}
		return out;
	}
}
