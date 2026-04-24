package com.nfy.cancerapp.bootstrap;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nfy.cancerapp.mapper.SupplementRuleMapper;
import com.nfy.cancerapp.model.SupplementRule;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 首次启动且规则表为空时写入示例数据（教育向，非临床指南全文）。
 */
@Component
@Order(100)
public class SupplementRuleDataLoader implements ApplicationRunner {

	private final SupplementRuleMapper supplementRuleMapper;

	public SupplementRuleDataLoader(SupplementRuleMapper supplementRuleMapper) {
		this.supplementRuleMapper = supplementRuleMapper;
	}

	@Override
	public void run(ApplicationArguments args) {
		long n = supplementRuleMapper.selectCount(new LambdaQueryWrapper<SupplementRule>());
		if (n > 0) {
			return;
		}
		for (SupplementRule r : builtIn()) {
			supplementRuleMapper.insert(r);
		}
	}

	private static List<SupplementRule> builtIn() {
		return List.of(
				rule("高剂量维生素C", "维生素C,VC,泡腾片", "氟尿嘧啶,5-FU", "CAUTION",
						"高剂量抗氧化剂可能与部分化疗药相互作用，疗效影响说法不一。",
						"务必先问主治医生或药师；不要自行大剂量补充。"),
				rule("绿茶/绿茶提取物", "绿茶,儿茶素,EGCG", "氟尿嘧啶", "CAUTION",
						"绿茶中成分可能影响部分药物吸收或代谢。",
						"化疗用药当天可与医护确认是否需间隔饮用。"),
				rule("葡萄柚与西柚", "西柚,葡萄柚,柚子汁", "伊立替康,依托泊苷,部分靶向药", "AVOID",
						"西柚类可影响体内药物代谢酶，导致血药浓度异常。",
						"用药期间避免西柚/葡萄柚及其果汁，除非医生明确说可以。"),
				rule("人参类制品", "人参,西洋参,红参", "", "CAUTION",
						"可能与凝血、血压、血糖等产生复杂影响。",
						"化疗或抗凝治疗期间使用前请咨询肿瘤科医生。"),
				rule("灵芝孢子粉", "灵芝,孢子粉", "", "CAUTION",
						"研究质量参差，与化疗相互作用证据有限但需谨慎。",
						"不要替代正规治疗；使用前与医生沟通。"),
				rule("中药汤剂或复方", "中药,汤剂,中成药", "", "CAUTION",
						"成分复杂，可能与化疗药发生未知相互作用。",
						"务必告知医生正在服用的所有中药，由医生/药师评估。"),
				rule("叶酸补充剂", "叶酸,叶酸盐", "甲氨蝶呤,MTX", "AVOID",
						"叶酸可拮抗甲氨蝶呤等叶酸拮抗剂类药的作用（若您在用此类药）。",
						"除非医生处方要求，勿自行补充叶酸。"),
				rule("辅酶Q10", "辅酶Q10,CoQ10", "多柔比星,阿霉素", "SAFE",
						"常规剂量下多数患者可耐受，仍有争议研究。",
						"仍建议告知医生；剂量遵医嘱。"),
				rule("钙片（常规剂量）", "钙,碳酸钙,柠檬酸钙", "", "SAFE",
						"一般饮食补充剂量多与化疗常规冲突风险低。",
						"与某些抗生素等同服可能需间隔，按药袋说明或药师指导。"),
				rule("乳清蛋白粉", "乳清蛋白,蛋白粉", "", "SAFE",
						"帮助补足蛋白摄入时常用。",
						"肾功能异常者需医生指导蛋白总量。"),
				rule("鱼油/DHA", "鱼油,DHA,EPA,深海鱼油", "奥沙利铂", "CAUTION",
						"高剂量鱼油可能影响凝血或与部分方案同用需谨慎。",
						"手术或血小板低时尤其要问医生。"),
				rule("大蒜素/大蒜提取物", "大蒜,大蒜素", "奥沙利铂", "CAUTION",
						"高剂量补充剂与抗凝、出血风险需综合评估。",
						"日常做菜用蒜一般问题不大；浓缩胶囊先问医生。"),
				rule("维生素E（高剂量）", "维生素E,生育酚", "", "CAUTION",
						"高剂量抗氧化剂在化疗期间争议较多。",
						"避免自行长期大剂量服用。"),
				rule("益生菌", "益生菌,乳酸菌", "", "SAFE",
						"多数用于肠道菌群调节，与化疗药直接冲突较少。",
						"免疫严重抑制或中心静脉置管感染风险高时遵医嘱。"),
				rule("铁剂", "铁,硫酸亚铁,多糖铁", "口服化疗药", "CAUTION",
						"部分口服药与铁剂同服可能影响吸收。",
						"间隔服用时间请按药师建议。"),
				rule("纳豆/纳豆激酶", "纳豆,纳豆激酶", "", "CAUTION",
						"可能与抗凝、抗血小板药有叠加出血风险。",
						"正在用抗凝药者务必问医生。"),
				rule("圣约翰草", "圣约翰草,贯叶连翘", "伊立替康,多西他赛,部分靶向药", "AVOID",
						"明确可诱导药物代谢酶，导致多种化疗/靶向药浓度下降。",
						"化疗期间不建议使用。"),
				rule("姜黄素补充剂", "姜黄素,姜黄", "伊立替康", "CAUTION",
						"高剂量补充剂与药物相互作用研究仍在积累。",
						"告知医生；不替代正规抗肿瘤治疗。")
		);
	}

	private static SupplementRule rule(String name, String tags, String drugKw, String risk,
	                                   String reason, String suggestion) {
		SupplementRule r = new SupplementRule();
		r.setSupplementName(name);
		r.setIngredientTags(tags);
		r.setDrugKeyword(drugKw);
		r.setRiskLevel(risk);
		r.setReason(reason);
		r.setSuggestion(suggestion);
		return r;
	}
}
