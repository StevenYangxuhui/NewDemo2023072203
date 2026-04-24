package com.nfy.cancerapp.regimen;

import com.nfy.cancerapp.model.ChemoRegimen;
import com.nfy.cancerapp.model.ChemoMedication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * 面向患者的「常见化疗方案」预设：通俗说明 + 参考用药（教育用途，非医嘱）。
 * 临床剂量与日程以医生方案为准。
 */
@Component("regimenCatalog")
public class RegimenTemplateCatalog {

	public static final String CODE_OTHER = "OTHER";

	public record MedLine(String drugName, String doseHint, String route, String frequency, Integer dayInCycle, String remark) {
		public ChemoMedication toEntity(Long regimenId) {
			ChemoMedication m = new ChemoMedication();
			m.setRegimenId(regimenId);
			m.setDrugName(drugName);
			m.setDose(doseHint);
			m.setRoute(route);
			m.setFrequency(frequency);
			m.setDayInCycle(dayInCycle);
			m.setRemark(remark);
			return m;
		}
	}

	public record TemplateOption(
			String code,
			String categoryKey,
			String categoryLabel,
			String title,
			String subtitle,
			String patientDescription,
			Integer typicalCycleDaysHint,
			Integer suggestedTotalCyclesHint,
			List<MedLine> referenceMedications
	) {
	}

	private static final List<TemplateOption> ALL;

	static {
		List<TemplateOption> list = new ArrayList<>();
		list.add(new TemplateOption(
				"FOLFOX",
				"digestive", "胃肠肿瘤",
				"FOLFOX",
				"奥沙利铂 + 亚叶酸钙 + 氟尿嘧啶",
				"很多结直肠癌病友会用到这一类方案，输液天数和剂量都是医生根据身体情况算的。您只要选对「大概像这一种」即可，方便我们给您饮食和保健品提醒。",
				14, 12,
				List.of(
						new MedLine("奥沙利铂", "剂量由医生按体表面积计算", "静脉输注", "按周期第1天", 1, "常见为每2周重复，具体以医嘱为准"),
						new MedLine("亚叶酸钙", "遵医嘱", "静脉输注", "常与氟尿嘧啶同日", 1, "用于增强氟尿嘧啶疗效"),
						new MedLine("氟尿嘧啶（5-FU）", "遵医嘱", "静脉持续泵入或分次", "第1–2天等，按医嘱", 1, "注射时间可能持续数十小时，以您所在医院流程为准")
				)));
		list.add(new TemplateOption(
				"FOLFIRI",
				"digestive", "胃肠肿瘤",
				"FOLFIRI",
				"伊立替康 + 亚叶酸钙 + 氟尿嘧啶",
				"也常用于结直肠癌等。若医生提到伊立替康和5-FU，可优先选这项。",
				14, 12,
				List.of(
						new MedLine("伊立替康", "剂量由医生计算", "静脉输注", "常于周期第1天", 1, "可能有腹泻等反应，以医护指导为准"),
						new MedLine("亚叶酸钙", "遵医嘱", "静脉输注", "同日", 1, null),
						new MedLine("氟尿嘧啶（5-FU）", "遵医嘱", "静脉", "按医嘱", 1, null)
				)));
		list.add(new TemplateOption(
				"CAPOX",
				"digestive", "胃肠肿瘤",
				"CAPOX / XELOX",
				"口服卡培他滨 + 奥沙利铂",
				"有一部分病友是「输液一天奥沙利铂，回家吃卡培他滨片」。若符合这种描述，选这里。",
				21, 8,
				List.of(
						new MedLine("奥沙利铂", "遵医嘱", "静脉输注", "常每3周第1天", 1, null),
						new MedLine("卡培他滨", "遵医嘱", "口服", "按医嘱连服多日", 1, "服药期间多喝水，具体天数听医生的")
				)));
		list.add(new TemplateOption(
				"AC",
				"breast", "乳腺",
				"AC 方案",
				"多柔比星 + 环磷酰胺",
				"乳腺癌辅助/新辅助化疗里很常见。若医生说是「红药水」一类联合环磷酰胺，可对照此项。",
				14, 4,
				List.of(
						new MedLine("多柔比星", "遵医嘱", "静脉输注", "按周期", 1, "俗称蒽环类药物，心脏毒性需医生监测"),
						new MedLine("环磷酰胺", "遵医嘱", "静脉输注", "按周期", 1, null)
				)));
		list.add(new TemplateOption(
				"TAC",
				"breast", "乳腺",
				"TAC 方案",
				"多西他赛 + 多柔比星 + 环磷酰胺",
				"三种药联合，多用于乳腺癌。名字里带「T（紫杉类）」又带蒽环时，可看是否接近此项。",
				21, 6,
				List.of(
						new MedLine("多西他赛", "遵医嘱", "静脉输注", "按医嘱", 1, null),
						new MedLine("多柔比星", "遵医嘱", "静脉输注", "按医嘱", 1, null),
						new MedLine("环磷酰胺", "遵医嘱", "静脉输注", "按医嘱", 1, null)
				)));
		list.add(new TemplateOption(
				"TC",
				"breast", "乳腺",
				"TC 方案",
				"多西他赛 + 环磷酰胺",
				"不少乳腺癌病友在术后会用到的双药方案，不含蒽环。",
				21, 4,
				List.of(
						new MedLine("多西他赛", "遵医嘱", "静脉输注", "按周期", 1, null),
						new MedLine("环磷酰胺", "遵医嘱", "静脉输注", "按周期", 1, null)
				)));
		list.add(new TemplateOption(
				"TP",
				"lung", "肺癌等",
				"TP 方案",
				"紫杉醇 + 顺铂",
				"常用于非小细胞肺癌等含铂双药方案。若医生写的是紫杉类+顺铂，可尝试选这个。",
				21, 4,
				List.of(
						new MedLine("紫杉醇", "遵医嘱", "静脉输注", "按周期", 1, "可能有过敏预防用药，听护士安排"),
						new MedLine("顺铂", "遵医嘱", "静脉输注", "按周期", 1, "多喝水、注意肾功能监测")
				)));
		list.add(new TemplateOption(
				"GP",
				"lung", "肺癌等",
				"GP 方案",
				"吉西他滨 + 顺铂",
				"肺癌、膀胱癌等多种实体瘤可能使用。两个药往往在周期内特定几天给药。",
				28, 4,
				List.of(
						new MedLine("吉西他滨", "遵医嘱", "静脉输注", "第1、8天等", 1, "具体给药日以医嘱为准"),
						new MedLine("顺铂", "遵医嘱", "静脉输注", "按医嘱", 8, "日期仅为示例")
				)));
		list.add(new TemplateOption(
				"EP",
				"lung", "肺癌等",
				"EP 方案",
				"依托泊苷 + 顺铂",
				"小细胞肺癌等常用。若医生提到「VP-16」和顺铂，通常接近此项。",
				21, 4,
				List.of(
						new MedLine("依托泊苷（VP-16）", "遵医嘱", "静脉输注", "连续数日可能", 1, null),
						new MedLine("顺铂", "遵医嘱", "静脉输注", "按医嘱", 1, null)
				)));
		list.add(new TemplateOption(
				"BEP",
				"other", "其他肿瘤",
				"BEP 方案",
				"博来霉素 + 依托泊苷 + 顺铂",
				"多用于生殖细胞肿瘤等。若医生明确说是 BEP，请选此项。",
				21, 3,
				List.of(
						new MedLine("博来霉素", "遵医嘱", "注射", "按周期", 1, "注意肺功能相关随访"),
						new MedLine("依托泊苷", "遵医嘱", "静脉输注", "按医嘱", 1, null),
						new MedLine("顺铂", "遵医嘱", "静脉输注", "按医嘱", 1, null)
				)));
		list.add(new TemplateOption(
				"R_CHOP",
				"lymphoma", "淋巴瘤",
				"R-CHOP",
				"利妥昔单抗联合 CHOP",
				"非霍奇金淋巴瘤非常经典的方案。名字里带「美罗华/R」和 CHOP 时选这里。",
				21, 6,
				List.of(
						new MedLine("利妥昔单抗", "遵医嘱", "静脉输注", "周期第1天", 1, "输注过程需医护观察"),
						new MedLine("环磷酰胺", "遵医嘱", "静脉输注", "按医嘱", 1, null),
						new MedLine("多柔比星", "遵医嘱", "静脉输注", "按医嘱", 1, null),
						new MedLine("长春新碱", "遵医嘱", "静脉注射", "按医嘱", 1, null),
						new MedLine("泼尼松", "遵医嘱", "口服", "连服数日", 1, "激素类，勿自行停药")
				)));
		list.add(new TemplateOption(
				"ABVD",
				"lymphoma", "淋巴瘤",
				"ABVD",
				"霍奇金淋巴瘤常用方案之一",
				"四种药物联合。若病历上写 ABVD，直接选这项即可。",
				28, 6,
				List.of(
						new MedLine("多柔比星", "遵医嘱", "静脉输注", "按周期", 1, null),
						new MedLine("博来霉素", "遵医嘱", "注射", "按周期", 1, null),
						new MedLine("长春碱", "遵医嘱", "静脉", "按周期", 1, null),
						new MedLine("达卡巴嗪", "遵医嘱", "静脉输注", "按周期", 1, null)
				)));
		list.add(new TemplateOption(
				"OTHER",
				"uncertain", "不确定 / 其他",
				"列表里没有我的方案",
				"由医生单独制定，药名和上面都不太一样",
				"没关系，您可以手写医生告诉您的方案简称或药名（下一页填写）。下面不会出现固定参考药单，用药请以医嘱和药袋为准。",
				null, null,
				List.of()));

		ALL = Collections.unmodifiableList(list);
	}

	public List<TemplateOption> allOptions() {
		return ALL;
	}

	public Optional<TemplateOption> findByCode(String code) {
		if (code == null || code.isBlank()) {
			return Optional.empty();
		}
		String c = code.trim().toUpperCase(Locale.ROOT);
		return ALL.stream().filter(t -> t.code().equalsIgnoreCase(c)).findFirst();
	}

	/**
	 * 列表卡片标题：有模板代码用标准名，否则用库里的方案名称。
	 */
	public String resolveListTitle(ChemoRegimen r) {
		if (r == null) {
			return "";
		}
		if (r.getTemplateCode() != null && !r.getTemplateCode().isBlank()) {
			Optional<TemplateOption> opt = findByCode(r.getTemplateCode());
			if (opt.isPresent()) {
				return opt.get().title();
			}
		}
		return r.getRegimenName() != null ? r.getRegimenName() : "化疗方案";
	}

	public String resolveSubtitle(ChemoRegimen r) {
		if (r == null || r.getTemplateCode() == null || r.getTemplateCode().isBlank()) {
			return null;
		}
		return findByCode(r.getTemplateCode()).map(TemplateOption::subtitle).orElse(null);
	}

	public Set<String> categoryLabels() {
		Set<String> set = new LinkedHashSet<>();
		for (TemplateOption t : ALL) {
			set.add(t.categoryLabel());
		}
		return set;
	}

	public boolean isKnownTemplateCode(String code) {
		return findByCode(code).isPresent();
	}
}
