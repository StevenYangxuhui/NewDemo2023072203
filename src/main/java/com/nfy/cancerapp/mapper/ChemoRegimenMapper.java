package com.nfy.cancerapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nfy.cancerapp.model.ChemoRegimen;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ChemoRegimenMapper extends BaseMapper<ChemoRegimen> {

	@Select("""
			SELECT cr.current_cycle
			FROM chemo_regimen cr
			INNER JOIN patient p ON cr.patient_id = p.id
			WHERE p.name = #{name}
			ORDER BY cr.start_date DESC, cr.id DESC
			LIMIT 1
			""")
	Integer selectCurrentCycleByPatientName(@Param("name") String name);
}
