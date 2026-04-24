package com.nfy.cancerapp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("supplement_rule")
public class SupplementRule {
	@TableId(type = IdType.AUTO)
	private Long id;
	private String supplementName;
	private String ingredientTags;
	private String drugKeyword;
	/** SAFE / CAUTION / AVOID */
	private String riskLevel;
	private String reason;
	private String suggestion;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSupplementName() {
		return supplementName;
	}

	public void setSupplementName(String supplementName) {
		this.supplementName = supplementName;
	}

	public String getIngredientTags() {
		return ingredientTags;
	}

	public void setIngredientTags(String ingredientTags) {
		this.ingredientTags = ingredientTags;
	}

	public String getDrugKeyword() {
		return drugKeyword;
	}

	public void setDrugKeyword(String drugKeyword) {
		this.drugKeyword = drugKeyword;
	}

	public String getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
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
