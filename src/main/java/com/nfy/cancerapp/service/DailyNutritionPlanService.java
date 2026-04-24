package com.nfy.cancerapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nfy.cancerapp.mapper.DailyNutritionPlanMapper;
import com.nfy.cancerapp.model.AdverseReaction;
import com.nfy.cancerapp.model.DailyNutritionPlan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailyNutritionPlanService {

	private final DailyNutritionPlanMapper dailyNutritionPlanMapper;
	private final RegimenService regimenService;

	public DailyNutritionPlanService(DailyNutritionPlanMapper dailyNutritionPlanMapper,
	                                 RegimenService regimenService) {
		this.dailyNutritionPlanMapper = dailyNutritionPlanMapper;
		this.regimenService = regimenService;
	}

	public List<DailyNutritionPlan> listForPatient(Long patientId) {
		return dailyNutritionPlanMapper.selectList(
				new LambdaQueryWrapper<DailyNutritionPlan>()
						.eq(DailyNutritionPlan::getPatientId, patientId)
						.orderByDesc(DailyNutritionPlan::getPlanDate)
						.orderByDesc(DailyNutritionPlan::getId));
	}

	public DailyNutritionPlan getOwned(Long id, Long patientId) {
		if (id == null || patientId == null) {
			return null;
		}
		DailyNutritionPlan p = dailyNutritionPlanMapper.selectById(id);
		if (p == null || !patientId.equals(p.getPatientId())) {
			return null;
		}
		return p;
	}

	@Transactional
	public void save(DailyNutritionPlan entity, Long patientId) {
		if (entity.getRegimenId() != null
				&& regimenService.getOwnedRegimen(entity.getRegimenId(), patientId) == null) {
			throw new IllegalArgumentException("所选方案不属于您，请重新选择");
		}
		entity.setPatientId(patientId);
		if (entity.getId() == null) {
			dailyNutritionPlanMapper.insert(entity);
		} else {
			if (getOwned(entity.getId(), patientId) == null) {
				throw new IllegalArgumentException("记录不存在或无权修改");
			}
			dailyNutritionPlanMapper.updateById(entity);
		}
	}

	@Transactional
	public boolean delete(Long id, Long patientId) {
		DailyNutritionPlan p = getOwned(id, patientId);
		if (p == null) {
			return false;
		}
		dailyNutritionPlanMapper.deleteById(id);
		return true;
	}

	/**
	 * 记录不良反应后，自动为当日生成或合并一条营养干预草稿（高蛋白、易消化等方向）。
	 */
	@Transactional
	public void mergeDailyPlanFromReaction(Long patientId, AdverseReaction reaction) {
		if (patientId == null || reaction == null || reaction.getReactionType() == null) {
			return;
		}
		LocalDate day = reaction.getRecordDate() != null ? reaction.getRecordDate() : LocalDate.now();
		DailyNutritionPlan existing = dailyNutritionPlanMapper.selectOne(
				new LambdaQueryWrapper<DailyNutritionPlan>()
						.eq(DailyNutritionPlan::getPatientId, patientId)
						.eq(DailyNutritionPlan::getPlanDate, day)
						.last("LIMIT 1"));
		String advice = dietAdviceForReaction(reaction.getReactionType());
		String sample = sampleMenuForReaction(reaction.getReactionType());
		String tag = "【根据「" + reaction.getReactionType() + "」自动调整】";
		if (existing == null) {
			DailyNutritionPlan n = new DailyNutritionPlan();
			n.setPatientId(patientId);
			n.setPlanDate(day);
			n.setPhase("DURING");
			n.setMainIssue(reaction.getReactionType());
			n.setDietAdvice(tag + advice);
			n.setSampleMenu(sample);
			dailyNutritionPlanMapper.insert(n);
		} else {
			String merged = existing.getDietAdvice() != null && !existing.getDietAdvice().isBlank()
					? existing.getDietAdvice() + "\n\n" + tag + advice
					: tag + advice;
			existing.setMainIssue(reaction.getReactionType());
			existing.setDietAdvice(merged);
			if (existing.getSampleMenu() == null || existing.getSampleMenu().isBlank()) {
				existing.setSampleMenu(sample);
			}
			dailyNutritionPlanMapper.updateById(existing);
		}
	}

	private static String dietAdviceForReaction(String type) {
		if (type.contains("恶心") || type.contains("呕吐")) {
			return "干湿分离、少量多餐；避免油腻与强烈气味；温热流质或半流质更易耐受。";
		}
		if (type.contains("腹泻")) {
			return "低脂、低渣、避免生冷与乳糖不耐受食物；注意补水与电解质（遵医嘱）。";
		}
		if (type.contains("口腔")) {
			return "软食、常温或微凉；避免酸、辣、硬、脆；注意口腔清洁与漱口。";
		}
		if (type.contains("乏力") || type.contains("食欲")) {
			return "优先保证热量与优质蛋白；可加餐，选择易吞咽、易消化的食物。";
		}
		if (type.contains("便秘")) {
			return "增加膳食纤维与水分（若医生允许）；适度活动。";
		}
		return "以耐受为准，少量多餐；有加重或脱水等情况及时联系医生。";
	}

	private static String sampleMenuForReaction(String type) {
		if (type.contains("恶心") || type.contains("呕吐")) {
			return "示例：大米粥/烂面条＋蒸蛋羹；苏打饼干少量；苹果泥（耐受时）。";
		}
		if (type.contains("腹泻")) {
			return "示例：白粥、馒头、香蕉泥；避免牛奶与高脂汤羹（个体有异，遵医嘱）。";
		}
		if (type.contains("口腔")) {
			return "示例：蒸蛋、豆腐脑、去刺鱼肉泥、南瓜泥。";
		}
		return "示例：清蒸鱼、蛋羹、软饭、煮软的蔬菜。";
	}
}
