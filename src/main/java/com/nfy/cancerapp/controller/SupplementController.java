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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 功能模块6：补充剂规则表 supplement_rule 的检索与按当前用药匹配（72203）。
 * 截图：/supplement-check（关键词搜索与规则列表）。
 */
@Controller
public class SupplementController {

	private final LoginPatient loginPatient;
	private final SupplementRuleService supplementRuleService;
	private final RegimenService regimenService;

	public SupplementController(LoginPatient loginPatient,
	                            SupplementRuleService supplementRuleService,
	                            RegimenService regimenService) {
		this.loginPatient = loginPatient;
		this.supplementRuleService = supplementRuleService;
		this.regimenService = regimenService;
	}

	@GetMapping("/supplement-check")
	public String page(@RequestParam(required = false) String q,
	                   HttpSession session,
	                   Model model,
	                   RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		model.addAttribute("patient", patient);
		model.addAttribute("q", q != null ? q : "");
		List<SupplementRule> nameHits = supplementRuleService.searchByUserKeyword(q);
		List<String> myDrugs = regimenService.listDistinctDrugNamesForPatient(patient.getId());
		model.addAttribute("myDrugs", myDrugs);
		model.addAttribute("nameHits", nameHits);
		List<SupplementRule> drugRelated = supplementRuleService.matchRulesForPatientDrugs(myDrugs);
		model.addAttribute("drugRelated", drugRelated);
		return "supplement-check";
	}
}
