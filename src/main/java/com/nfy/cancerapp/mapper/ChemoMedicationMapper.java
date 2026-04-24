package com.nfy.cancerapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nfy.cancerapp.model.ChemoMedication;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ChemoMedicationMapper extends BaseMapper<ChemoMedication> {

	@Select("""
			SELECT GROUP_CONCAT(DISTINCT cm.drug_name ORDER BY cm.drug_name SEPARATOR '，') AS drugs
			FROM chemo_medication cm
			INNER JOIN chemo_regimen cr ON cm.regimen_id = cr.id
			INNER JOIN patient p ON cr.patient_id = p.id
			WHERE p.name = #{name}
			  AND cm.day_in_cycle = cr.current_day
			""")
	String selectTodayDrugsConcatByPatientName(@Param("name") String name);
}
