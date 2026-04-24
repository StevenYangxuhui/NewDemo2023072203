package com.nfy.cancerapp.service;

import com.nfy.cancerapp.model.Patient;

/**
 * 根据年龄、性别、身高、体重估算每日热量与蛋白质需求（院内个体化公式以前，作 App 内参考）。
 */
public final class NutritionNeedsCalculator {

	private NutritionNeedsCalculator() {
	}

	/**
	 * 写入 patient 的 kcalNeed、proteinNeedG（有年龄+身高+体重时）。
	 */
	public static void apply(Patient p) {
		if (p == null || p.getAge() == null || p.getHeightCm() == null || p.getWeightKg() == null) {
			return;
		}
		if (p.getAge() < 10 || p.getAge() > 120) {
			return;
		}
		double w = p.getWeightKg();
		double h = p.getHeightCm();
		int age = p.getAge();
		double bmr;
		String g = p.getGender();
		if ("F".equalsIgnoreCase(g)) {
			bmr = 10 * w + 6.25 * h - 5 * age - 161;
		} else if ("M".equalsIgnoreCase(g)) {
			bmr = 10 * w + 6.25 * h - 5 * age + 5;
		} else {
			bmr = 10 * w + 6.25 * h - 5 * age - 78;
		}
		double activity = 1.2;
		double stress = 1.15;
		int kcal = (int) Math.round(bmr * activity * stress);
		double proteinG = Math.round(w * 1.3 * 10.0) / 10.0;
		p.setKcalNeed(Math.max(kcal, 800));
		p.setProteinNeedG(Math.max(proteinG, 30.0));
	}
}
