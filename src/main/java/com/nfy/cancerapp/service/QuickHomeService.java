package com.nfy.cancerapp.service;

import com.nfy.cancerapp.mapper.ChemoMedicationMapper;
import com.nfy.cancerapp.mapper.ChemoRegimenMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class QuickHomeService {

	private final ChemoRegimenMapper chemoRegimenMapper;
	private final ChemoMedicationMapper chemoMedicationMapper;

	public QuickHomeService(ChemoRegimenMapper chemoRegimenMapper,
	                        ChemoMedicationMapper chemoMedicationMapper) {
		this.chemoRegimenMapper = chemoRegimenMapper;
		this.chemoMedicationMapper = chemoMedicationMapper;
	}

	public Integer findCurrentCycleByPatientName(String name) {
		return chemoRegimenMapper.selectCurrentCycleByPatientName(name);
	}

	public String findTodayMedicationTipByPatientName(String name, LocalDate today) {
		// 仍用 current_day 近似「周期内第几天」；today 预留后续按日历推算
		return chemoMedicationMapper.selectTodayDrugsConcatByPatientName(name);
	}
}
