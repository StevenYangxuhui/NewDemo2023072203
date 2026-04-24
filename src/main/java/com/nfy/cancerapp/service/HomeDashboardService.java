package com.nfy.cancerapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nfy.cancerapp.mapper.ChemoRegimenMapper;
import com.nfy.cancerapp.model.ChemoRegimen;
import com.nfy.cancerapp.model.SupplementRule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeDashboardService {

	private final ChemoRegimenMapper chemoRegimenMapper;
	private final RegimenService regimenService;
	private final SupplementRuleService supplementRuleService;

	public HomeDashboardService(ChemoRegimenMapper chemoRegimenMapper,
	                            RegimenService regimenService,
	                            SupplementRuleService supplementRuleService) {
		this.chemoRegimenMapper = chemoRegimenMapper;
		this.regimenService = regimenService;
		this.supplementRuleService = supplementRuleService;
	}

	/** 当前化疗阶段一句话（基于最近方案的周期/天数的粗分类） */
	public String describeChemoStage(Long patientId) {
		if (patientId == null) {
			return "请先添加化疗方案并填写周期进度。";
		}
		ChemoRegimen cr = chemoRegimenMapper.selectOne(
				new LambdaQueryWrapper<ChemoRegimen>()
						.eq(ChemoRegimen::getPatientId, patientId)
						.orderByDesc(ChemoRegimen::getStartDate)
						.orderByDesc(ChemoRegimen::getId)
						.last("LIMIT 1"));
		if (cr == null) {
			return "尚未保存化疗方案，无法判断阶段。";
		}
		Integer day = cr.getCurrentDay();
		if (day == null && cr.getCurrentCycle() == null) {
			return "方案已选：可在「化疗方案」中补充周期与第几天，便于更准的阶段提示。";
		}
		if (day != null && day <= 2) {
			return "阶段参考：周期初段（用药集中期可能），饮食宜清淡、少量多餐。";
		}
		if (day != null && day >= 10) {
			return "阶段参考：周期后段，注意营养补足与口腔护理。";
		}
		return "阶段参考：化疗进行中，请结合医嘱与自身感受安排饮食。";
	}

	/** 今日饮食/营养风险一句提醒（结合方案药名与规则库） */
	public String buildNutritionRiskHint(Long patientId) {
		if (patientId == null) {
			return "登录并完善信息后，可在此查看简要风险提醒。";
		}
		List<String> drugs = regimenService.listDistinctDrugNamesForPatient(patientId);
		if (drugs.isEmpty()) {
			return "尚未从方案中识别到参考药名。添加化疗方案后，这里会结合规则库给出提醒。";
		}
		List<SupplementRule> matched = supplementRuleService.matchRulesForPatientDrugs(drugs);
		long avoid = matched.stream().filter(r -> "AVOID".equalsIgnoreCase(r.getRiskLevel())).count();
		long caution = matched.stream().filter(r -> "CAUTION".equalsIgnoreCase(r.getRiskLevel())).count();
		if (avoid > 0) {
			return "今日关注：根据您方案中的药名，知识库中存在「需特别注意」的保健品/食物相互作用，请到「营养与药物报告」查看详情。";
		}
		if (caution > 0) {
			return "今日关注：部分保健品或食物与当前用药可能存在慎用关系，建议先「查风险」或咨询药师。";
		}
		return "今日关注：暂未匹配到高危条目，仍建议新增保健品前先自查或问医生。";
	}
}
