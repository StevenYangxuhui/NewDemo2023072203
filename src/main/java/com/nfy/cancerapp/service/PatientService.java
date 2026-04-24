package com.nfy.cancerapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nfy.cancerapp.mapper.PatientMapper;
import com.nfy.cancerapp.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

	private final PatientMapper patientMapper;

	public PatientService(PatientMapper patientMapper) {
		this.patientMapper = patientMapper;
	}

	public Patient findByName(String name) {
		return patientMapper.selectOne(
				new LambdaQueryWrapper<Patient>()
						.eq(Patient::getName, name)
						.last("LIMIT 1"));
	}

	public Patient ensureExists(String name) {
		Patient existing = findByName(name);
		if (existing != null) {
			return existing;
		}
		Patient p = new Patient();
		p.setName(name);
		patientMapper.insert(p);
		return p;
	}

	public void saveOrUpdate(Patient form) {
		NutritionNeedsCalculator.apply(form);
		Patient existing = findByName(form.getName());
		if (existing == null) {
			patientMapper.insert(form);
		} else {
			form.setId(existing.getId());
			patientMapper.updateById(form);
		}
	}
}
