package com.nfy.cancerapp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("daily_nutrition_plan")
public class DailyNutritionPlan {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long patientId;
	private Long regimenId;
	private LocalDate planDate;
	/** PRE / DURING / POST */
	private String phase;
	private String mainIssue;
	private Integer energyKcal;
	private Double proteinG;
	private String dietAdvice;
	private String sampleMenu;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public Long getRegimenId() {
		return regimenId;
	}

	public void setRegimenId(Long regimenId) {
		this.regimenId = regimenId;
	}

	public LocalDate getPlanDate() {
		return planDate;
	}

	public void setPlanDate(LocalDate planDate) {
		this.planDate = planDate;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getMainIssue() {
		return mainIssue;
	}

	public void setMainIssue(String mainIssue) {
		this.mainIssue = mainIssue;
	}

	public Integer getEnergyKcal() {
		return energyKcal;
	}

	public void setEnergyKcal(Integer energyKcal) {
		this.energyKcal = energyKcal;
	}

	public Double getProteinG() {
		return proteinG;
	}

	public void setProteinG(Double proteinG) {
		this.proteinG = proteinG;
	}

	public String getDietAdvice() {
		return dietAdvice;
	}

	public void setDietAdvice(String dietAdvice) {
		this.dietAdvice = dietAdvice;
	}

	public String getSampleMenu() {
		return sampleMenu;
	}

	public void setSampleMenu(String sampleMenu) {
		this.sampleMenu = sampleMenu;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
