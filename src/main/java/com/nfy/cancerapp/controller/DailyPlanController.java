package com.nfy.cancerapp.controller;

import com.nfy.cancerapp.model.AdverseReaction;
import com.nfy.cancerapp.model.DailyNutritionPlan;
import com.nfy.cancerapp.model.Patient;
import com.nfy.cancerapp.service.AdverseReactionService;
import com.nfy.cancerapp.service.DailyNutritionPlanService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能模块5：每日营养计划表 daily_nutrition_plan 的列表、编辑、保存、删除（72203）。
 * 截图：/daily-plan、/daily-plan/edit。
 */
@Controller
public class DailyPlanController {

	private static final Map<String, String> PHASE_LABELS = new LinkedHashMap<>();

	static {
		PHASE_LABELS.put("PRE", "化疗前");
		PHASE_LABELS.put("DURING", "化疗中");
		PHASE_LABELS.put("POST", "化疗后");
	}

	private final LoginPatient loginPatient;
	private final DailyNutritionPlanService dailyNutritionPlanService;
	private final RegimenService regimenService;
	private final AdverseReactionService adverseReactionService;

	public DailyPlanController(LoginPatient loginPatient,
	                           DailyNutritionPlanService dailyNutritionPlanService,
	                           RegimenService regimenService,
	                           AdverseReactionService adverseReactionService) {
		this.loginPatient = loginPatient;
		this.dailyNutritionPlanService = dailyNutritionPlanService;
		this.regimenService = regimenService;
		this.adverseReactionService = adverseReactionService;
	}

	private static final List<PhaseBlock> PHASE_BLOCKS = List.of(
			new PhaseBlock("PRE", "化疗前",
					"以均衡、易消化为主，避免尝试陌生保健品；保证优质蛋白与适量热量。",
					"示例：瘦肉粥、蒸鱼、豆腐、少油青菜、水果（医生允许范围内）。"),
			new PhaseBlock("DURING", "化疗中",
					"少量多餐，足量饮水；恶心时干湿分离；腹泻时低脂低渣；注意口腔卫生。",
					"示例：烂面条、蒸蛋羹、酸奶（耐受时）、香蕉、去刺鱼肉。"),
			new PhaseBlock("POST", "化疗后",
					"逐步恢复多样化饮食，仍避免高脂辛辣；关注营养重建与体重变化。",
					"示例：软饭、清炖鸡、西兰花煮软、坚果碎（吞咽允许时）。")
	);

	public record PhaseBlock(String phaseKey, String phaseTitle, String dietText, String menuText) {
	}

	@GetMapping("/daily-plan")
	public String list(HttpSession session, Model model, RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		model.addAttribute("patient", patient);
		model.addAttribute("plans", dailyNutritionPlanService.listForPatient(patient.getId()));
		model.addAttribute("phaseLabels", PHASE_LABELS);
		model.addAttribute("phaseBlocks", PHASE_BLOCKS);
		AdverseReaction latest = adverseReactionService.findLatest(patient.getId());
		model.addAttribute("latestReaction", latest);
		return "daily-plan";
	}

	@GetMapping("/daily-plan/edit")
	public String edit(@RequestParam(required = false) Long id,
	                   HttpSession session,
	                   Model model,
	                   RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		DailyNutritionPlan p;
		if (id != null) {
			p = dailyNutritionPlanService.getOwned(id, patient.getId());
			if (p == null) {
				ra.addFlashAttribute("error", "记录不存在");
				return "redirect:/daily-plan";
			}
		} else {
			p = new DailyNutritionPlan();
			p.setPlanDate(LocalDate.now());
			p.setPhase("DURING");
		}
		model.addAttribute("patient", patient);
		model.addAttribute("plan", p);
		model.addAttribute("phaseLabels", PHASE_LABELS);
		model.addAttribute("regimens", regimenService.listRegimens(patient.getId()));
		return "daily-plan-form";
	}

	@PostMapping("/daily-plan/save")
	public String save(@RequestParam(required = false) Long id,
	                   @RequestParam String planDate,
	                   @RequestParam String phase,
	                   @RequestParam(required = false) String mainIssue,
	                   @RequestParam(required = false) Integer energyKcal,
	                   @RequestParam(required = false) Double proteinG,
	                   @RequestParam(required = false) String dietAdvice,
	                   @RequestParam(required = false) String sampleMenu,
	                   @RequestParam(required = false) String regimenId,
	                   HttpSession session,
	                   RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		DailyNutritionPlan e = new DailyNutritionPlan();
		e.setId(id);
		e.setPlanDate(LocalDate.parse(planDate));
		e.setPhase(phase);
		e.setMainIssue(emptyToNull(mainIssue));
		e.setEnergyKcal(energyKcal);
		e.setProteinG(proteinG);
		e.setDietAdvice(emptyToNull(dietAdvice));
		e.setSampleMenu(emptyToNull(sampleMenu));
		e.setRegimenId(parseLongOrNull(regimenId));
		try {
			dailyNutritionPlanService.save(e, patient.getId());
		} catch (IllegalArgumentException ex) {
			ra.addFlashAttribute("error", ex.getMessage());
			return "redirect:/daily-plan/edit" + (id != null ? "?id=" + id : "");
		}
		return "redirect:/daily-plan";
	}

	@PostMapping("/daily-plan/delete")
	public String delete(@RequestParam Long id, HttpSession session, RedirectAttributes ra) {
		Patient patient = loginPatient.require(session, ra);
		if (patient == null) {
			return "redirect:/login";
		}
		if (!dailyNutritionPlanService.delete(id, patient.getId())) {
			ra.addFlashAttribute("error", "删除失败");
		}
		return "redirect:/daily-plan";
	}

	private static String emptyToNull(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		return s.trim();
	}

	private static Long parseLongOrNull(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		return Long.parseLong(s.trim());
	}
}
