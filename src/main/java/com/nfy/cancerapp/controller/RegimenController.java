package com.nfy.cancerapp.controller;

import com.nfy.cancerapp.model.ChemoMedication;
import com.nfy.cancerapp.model.ChemoRegimen;
import com.nfy.cancerapp.model.Patient;
import com.nfy.cancerapp.regimen.RegimenTemplateCatalog;
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

/**
 * 功能模块2+3：化疗方案表 chemo_regimen 与用药明细表 chemo_medication 的增删改查（72203）。
 * 截图：/regimen、/regimen/setup、/regimen/progress、/regimen/meds、用药表单与删除结果页。
 */
@Controller
public class RegimenController {

	private final RegimenService regimenService;
	private final RegimenTemplateCatalog regimenTemplateCatalog;
	private final LoginPatient loginPatient;

	public RegimenController(RegimenService regimenService,
	                       RegimenTemplateCatalog regimenTemplateCatalog,
	                       LoginPatient loginPatient) {
		this.regimenService = regimenService;
		this.regimenTemplateCatalog = regimenTemplateCatalog;
		this.loginPatient = loginPatient;
	}

	private Patient requirePatient(HttpSession session, RedirectAttributes ra) {
		return loginPatient.require(session, ra);
	}

	@GetMapping("/regimen")
	public String list(HttpSession session, Model model, RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		model.addAttribute("patient", patient);
		model.addAttribute("regimens", regimenService.listRegimens(patient.getId()));
		model.addAttribute("medsByRegimen", regimenService.medicationsByRegimenForPatient(patient.getId()));
		model.addAttribute("regimenCatalog", regimenTemplateCatalog);
		return "regimen";
	}

	/** 旧入口：改为引导到「选择方案」 */
	@GetMapping("/regimen/form")
	public String legacyForm() {
		return "redirect:/regimen/choose";
	}

	@GetMapping("/regimen/choose")
	public String choose(HttpSession session, Model model, RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		model.addAttribute("patient", patient);
		model.addAttribute("templates", regimenTemplateCatalog.allOptions());
		model.addAttribute("categoryLabels", regimenTemplateCatalog.categoryLabels());
		return "regimen-choose";
	}

	@GetMapping("/regimen/setup")
	public String setup(@RequestParam String templateCode,
	                    HttpSession session,
	                    Model model,
	                    RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		var opt = regimenTemplateCatalog.findByCode(templateCode);
		if (opt.isEmpty()) {
			ra.addFlashAttribute("error", "没有这个方案选项，请重新选择");
			return "redirect:/regimen/choose";
		}
		model.addAttribute("patient", patient);
		model.addAttribute("template", opt.get());
		return "regimen-setup";
	}

	@PostMapping("/regimen/setup/save")
	public String setupSave(@RequestParam String templateCode,
	                        @RequestParam(required = false) String customRegimenName,
	                        @RequestParam(required = false) String startDate,
	                        @RequestParam(required = false) Integer totalCycles,
	                        @RequestParam(required = false) Integer currentCycle,
	                        @RequestParam(required = false) Integer currentDay,
	                        @RequestParam(required = false) String note,
	                        HttpSession session,
	                        RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		LocalDate sd = parseDateOrNull(startDate);
		try {
			regimenService.createFromTemplate(
					patient.getId(),
					templateCode,
					customRegimenName,
					sd,
					totalCycles,
					currentCycle,
					currentDay,
					note);
		} catch (IllegalArgumentException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
			return "redirect:/regimen/setup?templateCode=" + templateCode;
		}
		return "redirect:/regimen";
	}

	@GetMapping("/regimen/progress")
	public String progress(@RequestParam Long regimenId,
	                       HttpSession session,
	                       Model model,
	                       RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		ChemoRegimen regimen = regimenService.getOwnedRegimen(regimenId, patient.getId());
		if (regimen == null) {
			ra.addFlashAttribute("error", "找不到该方案或无权访问");
			return "redirect:/regimen";
		}
		model.addAttribute("patient", patient);
		model.addAttribute("regimen", regimen);
		model.addAttribute("regimenCatalog", regimenTemplateCatalog);
		return "regimen-progress";
	}

	@PostMapping("/regimen/progress/save")
	public String progressSave(@RequestParam Long regimenId,
	                           @RequestParam(required = false) String startDate,
	                           @RequestParam(required = false) Integer totalCycles,
	                           @RequestParam(required = false) Integer currentCycle,
	                           @RequestParam(required = false) Integer currentDay,
	                           @RequestParam(required = false) String note,
	                           HttpSession session,
	                           RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		try {
			regimenService.updatePatientProgress(
					regimenId,
					patient.getId(),
					parseDateOrNull(startDate),
					totalCycles,
					currentCycle,
					currentDay,
					note);
		} catch (IllegalArgumentException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
			return "redirect:/regimen/progress?regimenId=" + regimenId;
		}
		return "redirect:/regimen";
	}

	@PostMapping("/regimen/delete")
	public String deleteRegimen(@RequestParam Long regimenId,
	                            HttpSession session,
	                            RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		if (!regimenService.deleteRegimen(regimenId, patient.getId())) {
			ra.addFlashAttribute("error", "删除失败：记录不存在或无权操作");
		}
		return "redirect:/regimen";
	}

	@GetMapping("/regimen/meds")
	public String medications(@RequestParam Long regimenId,
	                          HttpSession session,
	                          Model model,
	                          RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		ChemoRegimen regimen = regimenService.getOwnedRegimen(regimenId, patient.getId());
		if (regimen == null) {
			ra.addFlashAttribute("error", "找不到该方案或无权访问");
			return "redirect:/regimen";
		}
		model.addAttribute("patient", patient);
		model.addAttribute("regimen", regimen);
		model.addAttribute("medications", regimenService.listMedications(regimenId, patient.getId()));
		model.addAttribute("regimenCatalog", regimenTemplateCatalog);
		return "regimen-meds";
	}

	@GetMapping("/regimen/med/form")
	public String medForm(@RequestParam Long regimenId,
	                      @RequestParam(required = false) Long medId,
	                      HttpSession session,
	                      Model model,
	                      RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		ChemoRegimen regimen = regimenService.getOwnedRegimen(regimenId, patient.getId());
		if (regimen == null) {
			ra.addFlashAttribute("error", "找不到该方案或无权访问");
			return "redirect:/regimen";
		}
		ChemoMedication med;
		if (medId != null) {
			med = regimenService.getOwnedMedication(medId, patient.getId());
			if (med == null || !regimenId.equals(med.getRegimenId())) {
				ra.addFlashAttribute("error", "找不到该用药记录或无权访问");
				return "redirect:/regimen/meds?regimenId=" + regimenId;
			}
		} else {
			med = new ChemoMedication();
			med.setRegimenId(regimenId);
		}
		model.addAttribute("patient", patient);
		model.addAttribute("regimen", regimen);
		model.addAttribute("med", med);
		return "med-form";
	}

	@PostMapping("/regimen/med/save")
	public String saveMed(@RequestParam(required = false) Long id,
	                      @RequestParam Long regimenId,
	                      @RequestParam String drugName,
	                      @RequestParam(required = false) String dose,
	                      @RequestParam(required = false) String route,
	                      @RequestParam(required = false) String frequency,
	                      @RequestParam(required = false) Integer dayInCycle,
	                      @RequestParam(required = false) String remark,
	                      HttpSession session,
	                      RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		if (drugName == null || drugName.isBlank()) {
			ra.addFlashAttribute("error", "请填写药物名称");
			return "redirect:/regimen/med/form?regimenId=" + regimenId
					+ (id != null ? "&medId=" + id : "");
		}
		ChemoMedication m = new ChemoMedication();
		m.setId(id);
		m.setRegimenId(regimenId);
		m.setDrugName(drugName.trim());
		m.setDose(emptyToNull(dose));
		m.setRoute(emptyToNull(route));
		m.setFrequency(emptyToNull(frequency));
		m.setDayInCycle(dayInCycle);
		m.setRemark(emptyToNull(remark));
		try {
			regimenService.saveMedication(m, patient.getId());
		} catch (IllegalArgumentException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
			return "redirect:/regimen/meds?regimenId=" + regimenId;
		}
		return "redirect:/regimen/meds?regimenId=" + regimenId;
	}

	@PostMapping("/regimen/med/delete")
	public String deleteMed(@RequestParam Long medId,
	                        @RequestParam Long regimenId,
	                        HttpSession session,
	                        RedirectAttributes ra) {
		Patient patient = requirePatient(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		if (!regimenService.deleteMedication(medId, patient.getId())) {
			ra.addFlashAttribute("error", "删除失败：记录不存在或无权操作");
		}
		return "redirect:/regimen/meds?regimenId=" + regimenId;
	}

	private static LocalDate parseDateOrNull(String startDate) {
		if (startDate == null || startDate.isBlank()) {
			return null;
		}
		return LocalDate.parse(startDate);
	}

	private static String emptyToNull(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		return s.trim();
	}
}
