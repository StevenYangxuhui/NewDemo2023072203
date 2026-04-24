package com.nfy.cancerapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nfy.cancerapp.mapper.SupplementRuleMapper;
import com.nfy.cancerapp.model.SupplementRule;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class SupplementRuleService {

	private final SupplementRuleMapper supplementRuleMapper;

	public SupplementRuleService(SupplementRuleMapper supplementRuleMapper) {
		this.supplementRuleMapper = supplementRuleMapper;
	}

	public List<SupplementRule> listAll() {
		return supplementRuleMapper.selectList(
				new LambdaQueryWrapper<SupplementRule>().orderByAsc(SupplementRule::getRiskLevel).orderByAsc(SupplementRule::getId));
	}

	/** 按用户输入关键字模糊匹配保健品名、成分标签、药物关键词 */
	public List<SupplementRule> searchByUserKeyword(String q) {
		if (q == null || q.isBlank()) {
			return List.of();
		}
		String term = q.trim();
		return supplementRuleMapper.selectList(
				new LambdaQueryWrapper<SupplementRule>()
						.and(w -> w.like(SupplementRule::getSupplementName, term)
								.or().like(SupplementRule::getIngredientTags, term)
								.or().like(SupplementRule::getDrugKeyword, term))
						.orderByAsc(SupplementRule::getRiskLevel));
	}

	/**
	 * 与当前患者「方案参考药名」匹配的规则：仅 drug_keyword 非空且与任一药名包含匹配（不区分大小写）。
	 * 关键词中可含多个别名，用逗号分隔，任一命中即算匹配。
	 */
	public List<SupplementRule> matchRulesForPatientDrugs(List<String> patientDrugNames) {
		List<SupplementRule> all = listAll();
		if (all.isEmpty() || patientDrugNames == null || patientDrugNames.isEmpty()) {
			return List.of();
		}
		List<String> drugsLower = patientDrugNames.stream()
				.filter(s -> s != null && !s.isBlank())
				.map(s -> s.toLowerCase(Locale.ROOT))
				.toList();
		if (drugsLower.isEmpty()) {
			return List.of();
		}
		Set<SupplementRule> out = new LinkedHashSet<>();
		for (SupplementRule rule : all) {
			String kw = rule.getDrugKeyword();
			if (kw == null || kw.isBlank()) {
				continue;
			}
			String[] parts = kw.split("[,，、]");
			for (String part : parts) {
				String token = part.trim().toLowerCase(Locale.ROOT);
				if (token.isEmpty()) {
					continue;
				}
				for (String drug : drugsLower) {
					if (drug.contains(token) || token.contains(drug)) {
						out.add(rule);
						break;
					}
				}
				if (out.contains(rule)) {
					break;
				}
			}
		}
		List<SupplementRule> list = new ArrayList<>(out);
		list.sort(Comparator
				.comparing((SupplementRule r) -> riskOrder(r.getRiskLevel()))
				.thenComparing(SupplementRule::getId, Comparator.nullsLast(Long::compareTo)));
		return list;
	}

	/** 通用提醒：无具体药物关键词的规则里，取慎用/禁用若干条展示 */
	public List<SupplementRule> briefGeneralTips(int max) {
		List<SupplementRule> all = listAll();
		List<SupplementRule> pick = new ArrayList<>();
		for (SupplementRule r : all) {
			if (r.getDrugKeyword() != null && !r.getDrugKeyword().isBlank()) {
				continue;
			}
			String lv = r.getRiskLevel();
			if (lv != null && ("CAUTION".equalsIgnoreCase(lv) || "AVOID".equalsIgnoreCase(lv))) {
				pick.add(r);
			}
			if (pick.size() >= max) {
				break;
			}
		}
		return pick;
	}

	private static int riskOrder(String level) {
		if (level == null) {
			return 9;
		}
		return switch (level.toUpperCase(Locale.ROOT)) {
			case "AVOID" -> 0;
			case "CAUTION" -> 1;
			case "SAFE" -> 2;
			default -> 3;
		};
	}
}
