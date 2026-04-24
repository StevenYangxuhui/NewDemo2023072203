package com.nfy.cancerapp.controller;

import com.nfy.cancerapp.model.AdverseReaction;
import com.nfy.cancerapp.model.Patient;
import com.nfy.cancerapp.service.AdverseReactionService;
import com.nfy.cancerapp.service.RegimenService;
import com.nfy.cancerapp.web.LoginPatient;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * 功能模块4：不良反应表 adverse_reaction 的列表、编辑、保存、删除（72203）。
 * 截图：/reaction、/reaction/edit 及保存后列表。
 */
@Controller
public class ReactionController {

	private static final List<String> REACTION_TYPES = List.of(
			"恶心", "呕吐", "腹泻", "乏力", "口腔溃疡", "脱发", "手足麻木", "皮疹",
			"食欲下降", "便秘", "发热", "其他");

	private final LoginPatient loginPatient;
	private final AdverseReactionService adverseReactionService;
	private final RegimenService regimenService;

	public ReactionController(LoginPatient loginPatient,
	                          AdverseReactionService adverseReactionService,
	                          RegimenService regimenService) {
		this.loginPatient = loginPatient;
		this.adverseReactionService = adverseReactionService;
		this.regimenService = regimenService;
	}

	@GetMapping("/reaction")
	public String list(HttpSession session, Model model, RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		model.addAttribute("patient", patient);
		model.addAttribute("reactions", adverseReactionService.listForPatient(patient.getId()));
		model.addAttribute("regimens", regimenService.listRegimens(patient.getId()));
		return "reaction";
	}

	@GetMapping("/reaction/edit")
	public String edit(@RequestParam(required = false) Long id,
	                   HttpSession session,
	                   Model model,
	                   RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		AdverseReaction r;
		if (id != null) {
			r = adverseReactionService.getOwned(id, patient.getId());
			if (r == null) {
				ra.addFlashAttribute("error", "记录不存在");
				return "redirect:/reaction";
			}
		} else {
			r = new AdverseReaction();
			r.setRecordDate(LocalDate.now());
		}
		model.addAttribute("patient", patient);
		model.addAttribute("reaction", r);
		model.addAttribute("reactionTypes", REACTION_TYPES);
		model.addAttribute("regimens", regimenService.listRegimens(patient.getId()));
		return "reaction-form";
	}

	@PostMapping("/reaction/save")
	public String save(@RequestParam(required = false) Long id,
	                   @RequestParam String recordDate,
	                   @RequestParam String reactionType,
	                   @RequestParam(required = false) Integer severity,
	                   @RequestParam(required = false) String detail,
	                   @RequestParam(required = false) String regimenId,
	                   HttpSession session,
	                   RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		AdverseReaction e = new AdverseReaction();
		e.setId(id);
		e.setRecordDate(LocalDate.parse(recordDate));
		e.setReactionType(reactionType);
		e.setSeverity(severity);
		e.setDetail(detail != null && !detail.isBlank() ? detail.trim() : null);
		e.setRegimenId(parseLongOrNull(regimenId));
		try {
			adverseReactionService.save(e, patient.getId());
		} catch (IllegalArgumentException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
			return "redirect:/reaction/edit" + (id != null ? "?id=" + id : "");
		}
		return "redirect:/reaction";
	}

	@PostMapping("/reaction/delete")
	public String delete(@RequestParam Long id, HttpSession session, RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		if (!adverseReactionService.delete(id, patient.getId())) {
			ra.addFlashAttribute("error", "删除失败");
		}
		return "redirect:/reaction";
	}

	private static Long parseLongOrNull(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		return Long.parseLong(s.trim());
	}
}
