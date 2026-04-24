package com.nfy.cancerapp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("patient")
public class Patient {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private Double heightCm;
    private Double weightKg;
    private String tumorType;
    private String liverFunction;
    private String kidneyFunction;
    private String allergyHistory;
    private String dietTaboo;
    private Integer kcalNeed;
    private Double proteinNeedG;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public String getTumorType() {
        return tumorType;
    }

    public void setTumorType(String tumorType) {
        this.tumorType = tumorType;
    }

    public String getLiverFunction() {
        return liverFunction;
    }

    public void setLiverFunction(String liverFunction) {
        this.liverFunction = liverFunction;
    }

    public String getKidneyFunction() {
        return kidneyFunction;
    }

    public void setKidneyFunction(String kidneyFunction) {
        this.kidneyFunction = kidneyFunction;
    }

    public String getAllergyHistory() {
        return allergyHistory;
    }

    public void setAllergyHistory(String allergyHistory) {
        this.allergyHistory = allergyHistory;
    }

    public String getDietTaboo() {
        return dietTaboo;
    }

    public void setDietTaboo(String dietTaboo) {
        this.dietTaboo = dietTaboo;
    }

    public Integer getKcalNeed() {
        return kcalNeed;
    }

    public void setKcalNeed(Integer kcalNeed) {
        this.kcalNeed = kcalNeed;
    }

    public Double getProteinNeedG() {
        return proteinNeedG;
    }

    public void setProteinNeedG(Double proteinNeedG) {
        this.proteinNeedG = proteinNeedG;
    }
}

