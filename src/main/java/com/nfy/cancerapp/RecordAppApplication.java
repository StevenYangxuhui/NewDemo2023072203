package com.nfy.cancerapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CancerApp 入口。开发者 Yang SiQing，学号后五位标识 72203。
 * 功能模块与主入口对应：①患者表 patient ②化疗方案表 chemo_regimen ③用药明细表 chemo_medication
 * ④不良反应表 adverse_reaction ⑤每日营养计划表 daily_nutrition_plan ⑥补充剂规则表 supplement_rule（查询/匹配）
 * 数据库访问：MyBatis-Plus + MySQL；人工智能 API 需单独实现并在报告中截图说明。
 */
@SpringBootApplication
public class RecordAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecordAppApplication.class, args);
	}

}
