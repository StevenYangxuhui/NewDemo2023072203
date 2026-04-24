package com.nfy.cancerapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nfy.cancerapp.mapper.AdverseReactionMapper;
import com.nfy.cancerapp.model.AdverseReaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdverseReactionService {

	private final AdverseReactionMapper adverseReactionMapper;
	private final RegimenService regimenService;
	private final DailyNutritionPlanService dailyNutritionPlanService;

	public AdverseReactionService(AdverseReactionMapper adverseReactionMapper,
	                              RegimenService regimenService,
	                              DailyNutritionPlanService dailyNutritionPlanService) {
		this.adverseReactionMapper = adverseReactionMapper;
		this.regimenService = regimenService;
		this.dailyNutritionPlanService = dailyNutritionPlanService;
	}

	public List<AdverseReaction> listForPatient(Long patientId) {
		return adverseReactionMapper.selectList(
				new LambdaQueryWrapper<AdverseReaction>()
						.eq(AdverseReaction::getPatientId, patientId)
						.orderByDesc(AdverseReaction::getRecordDate)
						.orderByDesc(AdverseReaction::getId));
	}

	public AdverseReaction findLatest(Long patientId) {
		return adverseReactionMapper.selectOne(
				new LambdaQueryWrapper<AdverseReaction>()
						.eq(AdverseReaction::getPatientId, patientId)
						.orderByDesc(AdverseReaction::getRecordDate)
						.orderByDesc(AdverseReaction::getId)
						.last("LIMIT 1"));
	}

	public AdverseReaction getOwned(Long id, Long patientId) {
		if (id == null || patientId == null) {
			return null;
		}
		AdverseReaction r = adverseReactionMapper.selectById(id);
		if (r == null || !patientId.equals(r.getPatientId())) {
			return null;
		}
		return r;
	}

	@Transactional
	public void save(AdverseReaction entity, Long patientId) {
		if (entity.getRegimenId() != null
				&& regimenService.getOwnedRegimen(entity.getRegimenId(), patientId) == null) {
			throw new IllegalArgumentException("所选方案不属于您，请重新选择");
		}
		entity.setPatientId(patientId);
		if (entity.getId() == null) {
			adverseReactionMapper.insert(entity);
		} else {
			if (getOwned(entity.getId(), patientId) == null) {
				throw new IllegalArgumentException("记录不存在或无权修改");
			}
			adverseReactionMapper.updateById(entity);
		}
		dailyNutritionPlanService.mergeDailyPlanFromReaction(patientId, entity);
	}

	@Transactional
	public boolean delete(Long id, Long patientId) {
		AdverseReaction r = getOwned(id, patientId);
		if (r == null) {
			return false;
		}
		adverseReactionMapper.deleteById(id);
		return true;
	}
}
