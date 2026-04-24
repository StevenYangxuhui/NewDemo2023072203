package com.nfy.cancerapp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("adverse_reaction")
public class AdverseReaction {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long patientId;
	private Long regimenId;
	private LocalDate recordDate;
	private String reactionType;
	private Integer severity;
	private String detail;
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

	public LocalDate getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(LocalDate recordDate) {
		this.recordDate = recordDate;
	}

	public String getReactionType() {
		return reactionType;
	}

	public void setReactionType(String reactionType) {
		this.reactionType = reactionType;
	}

	public Integer getSeverity() {
		return severity;
	}

	public void setSeverity(Integer severity) {
		this.severity = severity;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
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
