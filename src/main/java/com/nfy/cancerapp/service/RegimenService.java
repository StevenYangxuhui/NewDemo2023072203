package com.nfy.cancerapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nfy.cancerapp.mapper.ChemoMedicationMapper;
import com.nfy.cancerapp.mapper.ChemoRegimenMapper;
import com.nfy.cancerapp.model.ChemoMedication;
import com.nfy.cancerapp.model.ChemoRegimen;
import com.nfy.cancerapp.model.Patient;
import com.nfy.cancerapp.regimen.RegimenTemplateCatalog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RegimenService {

	private final ChemoRegimenMapper chemoRegimenMapper;
	private final ChemoMedicationMapper chemoMedicationMapper;
	private final PatientService patientService;
	private final RegimenTemplateCatalog regimenTemplateCatalog;

	public RegimenService(ChemoRegimenMapper chemoRegimenMapper,
	                      ChemoMedicationMapper chemoMedicationMapper,
	                      PatientService patientService,
	                      RegimenTemplateCatalog regimenTemplateCatalog) {
		this.chemoRegimenMapper = chemoRegimenMapper;
		this.chemoMedicationMapper = chemoMedicationMapper;
		this.patientService = patientService;
		this.regimenTemplateCatalog = regimenTemplateCatalog;
	}

	public Patient resolveSessionPatient(String userName) {
		if (userName == null || userName.isBlank()) {
			return null;
		}
		return patientService.findByName(userName.trim());
	}

	public List<ChemoRegimen> listRegimens(Long patientId) {
		return chemoRegimenMapper.selectList(
				new LambdaQueryWrapper<ChemoRegimen>()
						.eq(ChemoRegimen::getPatientId, patientId)
						.orderByDesc(ChemoRegimen::getStartDate)
						.orderByDesc(ChemoRegimen::getId));
	}

	public ChemoRegimen getOwnedRegimen(Long regimenId, Long patientId) {
		if (regimenId == null || patientId == null) {
			return null;
		}
		ChemoRegimen r = chemoRegimenMapper.selectById(regimenId);
		if (r == null || !patientId.equals(r.getPatientId())) {
			return null;
		}
		return r;
	}

	/**
	 * 患者从预设模板创建方案，并自动生成「参考用药」清单（教育用途，非修改医嘱）。
	 */
	@Transactional
	public ChemoRegimen createFromTemplate(Long patientId,
	                                       String templateCode,
	                                       String customNameIfOther,
	                                       LocalDate startDate,
	                                       Integer totalCycles,
	                                       Integer currentCycle,
	                                       Integer currentDay,
	                                       String note) {
		RegimenTemplateCatalog.TemplateOption opt = regimenTemplateCatalog.findByCode(templateCode)
				.orElseThrow(() -> new IllegalArgumentException("未找到该方案类型，请返回重新选择"));
		if (RegimenTemplateCatalog.CODE_OTHER.equalsIgnoreCase(opt.code())) {
			if (customNameIfOther == null || customNameIfOther.isBlank()) {
				throw new IllegalArgumentException("请填写医生告诉您的方案名称或主要药名");
			}
		}
		ChemoRegimen r = new ChemoRegimen();
		r.setPatientId(patientId);
		r.setTemplateCode(opt.code());
		r.setRegimenName(RegimenTemplateCatalog.CODE_OTHER.equalsIgnoreCase(opt.code())
				? customNameIfOther.trim()
				: opt.title());
		r.setStartDate(startDate);
		if (totalCycles != null) {
			r.setTotalCycles(totalCycles);
		} else {
			r.setTotalCycles(opt.suggestedTotalCyclesHint());
		}
		r.setCurrentCycle(currentCycle);
		r.setCurrentDay(currentDay);
		r.setNote(note != null && !note.isBlank() ? note.trim() : null);
		chemoRegimenMapper.insert(r);
		for (RegimenTemplateCatalog.MedLine line : opt.referenceMedications()) {
			chemoMedicationMapper.insert(line.toEntity(r.getId()));
		}
		return r;
	}

	/** 仅更新「进度类」字段，不改方案名与模板代码。 */
	@Transactional
	public void updatePatientProgress(Long regimenId,
	                                  Long patientId,
	                                  LocalDate startDate,
	                                  Integer totalCycles,
	                                  Integer currentCycle,
	                                  Integer currentDay,
	                                  String note) {
		ChemoRegimen e = getOwnedRegimen(regimenId, patientId);
		if (e == null) {
			throw new IllegalArgumentException("方案不存在或无权操作");
		}
		e.setStartDate(startDate);
		e.setTotalCycles(totalCycles);
		e.setCurrentCycle(currentCycle);
		e.setCurrentDay(currentDay);
		e.setNote(note != null && !note.isBlank() ? note.trim() : null);
		chemoRegimenMapper.updateById(e);
	}

	@Transactional
	public void saveRegimen(ChemoRegimen entity, Long patientId) {
		if (entity.getId() == null) {
			entity.setPatientId(patientId);
			chemoRegimenMapper.insert(entity);
		} else {
			ChemoRegimen existing = getOwnedRegimen(entity.getId(), patientId);
			if (existing == null) {
				throw new IllegalArgumentException("方案不存在或无权操作");
			}
			entity.setPatientId(patientId);
			chemoRegimenMapper.updateById(entity);
		}
	}

	@Transactional
	public boolean deleteRegimen(Long regimenId, Long patientId) {
		ChemoRegimen r = getOwnedRegimen(regimenId, patientId);
		if (r == null) {
			return false;
		}
		chemoRegimenMapper.deleteById(regimenId);
		return true;
	}

	public List<ChemoMedication> listMedications(Long regimenId, Long patientId) {
		if (getOwnedRegimen(regimenId, patientId) == null) {
			return Collections.emptyList();
		}
		return chemoMedicationMapper.selectList(
				new LambdaQueryWrapper<ChemoMedication>()
						.eq(ChemoMedication::getRegimenId, regimenId)
						.orderByAsc(ChemoMedication::getDayInCycle)
						.orderByAsc(ChemoMedication::getId));
	}

	public ChemoMedication getOwnedMedication(Long medicationId, Long patientId) {
		if (medicationId == null || patientId == null) {
			return null;
		}
		ChemoMedication m = chemoMedicationMapper.selectById(medicationId);
		if (m == null) {
			return null;
		}
		return getOwnedRegimen(m.getRegimenId(), patientId) != null ? m : null;
	}

	@Transactional
	public void saveMedication(ChemoMedication entity, Long patientId) {
		if (getOwnedRegimen(entity.getRegimenId(), patientId) == null) {
			throw new IllegalArgumentException("方案不存在或无权操作");
		}
		if (entity.getId() == null) {
			chemoMedicationMapper.insert(entity);
		} else {
			if (getOwnedMedication(entity.getId(), patientId) == null) {
				throw new IllegalArgumentException("用药记录不存在或无权操作");
			}
			chemoMedicationMapper.updateById(entity);
		}
	}

	@Transactional
	public boolean deleteMedication(Long medicationId, Long patientId) {
		ChemoMedication m = getOwnedMedication(medicationId, patientId);
		if (m == null) {
			return false;
		}
		chemoMedicationMapper.deleteById(medicationId);
		return true;
	}

	/** 当前患者所有方案下的参考药名（去重、排序），供相互作用与自查使用 */
	public List<String> listDistinctDrugNamesForPatient(Long patientId) {
		List<ChemoRegimen> rs = listRegimens(patientId);
		if (rs.isEmpty()) {
			return Collections.emptyList();
		}
		List<Long> ids = rs.stream().map(ChemoRegimen::getId).toList();
		List<ChemoMedication> meds = chemoMedicationMapper.selectList(
				new LambdaQueryWrapper<ChemoMedication>().in(ChemoMedication::getRegimenId, ids));
		return meds.stream()
				.map(ChemoMedication::getDrugName)
				.filter(Objects::nonNull)
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.distinct()
				.sorted()
				.toList();
	}

	/** 各方案下的参考用药列表（同一页展示用） */
	public Map<Long, List<ChemoMedication>> medicationsByRegimenForPatient(Long patientId) {
		List<ChemoRegimen> rs = listRegimens(patientId);
		Map<Long, List<ChemoMedication>> map = new LinkedHashMap<>();
		for (ChemoRegimen r : rs) {
			map.put(r.getId(), listMedications(r.getId(), patientId));
		}
		return map;
	}
}
