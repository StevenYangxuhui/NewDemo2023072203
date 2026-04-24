package com.nfy.cancerapp.controller;

import com.nfy.cancerapp.model.Patient;
import com.nfy.cancerapp.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 功能模块1：患者表 patient 的查看与保存
 * 作者：杨思清
 * 学号：2023072203
 */
@Controller
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patient")
    public String page(HttpSession session, Model model) {
        String name = (String) session.getAttribute("userName");
        if (name == null || name.isBlank()) {
            return "redirect:/login";
        }
        Patient patient = patientService.findByName(name);
        model.addAttribute("name", name);
        model.addAttribute("patient", patient);
        return "patient";
    }

    @GetMapping("/patient/edit")
    public String editPage(HttpSession session, Model model) {
        String name = (String) session.getAttribute("userName");
        if (name == null || name.isBlank()) {
            return "redirect:/login";
        }
        Patient patient = patientService.findByName(name);
        if (patient == null) {
            patient = new Patient();
            patient.setName(name);
        }
        model.addAttribute("patient", patient);
        return "patient-form";
    }

    @PostMapping("/patient/save")
    public String save(@RequestParam String name,
                       @RequestParam(required = false) String gender,
                       @RequestParam(required = false) Integer age,
                       @RequestParam(required = false, name = "heightCm") Double heightCm,
                       @RequestParam(required = false, name = "weightKg") Double weightKg,
                       @RequestParam(required = false, name = "tumorType") String tumorType,
                       @RequestParam(required = false, name = "liverFunction") String liverFunction,
                       @RequestParam(required = false, name = "kidneyFunction") String kidneyFunction,
                       @RequestParam(required = false, name = "allergyHistory") String allergyHistory,
                       @RequestParam(required = false, name = "dietTaboo") String dietTaboo) {
        Patient p = new Patient();
        p.setName(name);
        p.setGender(gender);
        p.setAge(age);
        p.setHeightCm(heightCm);
        p.setWeightKg(weightKg);
        p.setTumorType(tumorType);
        p.setLiverFunction(liverFunction);
        p.setKidneyFunction(kidneyFunction);
        p.setAllergyHistory(allergyHistory);
        p.setDietTaboo(dietTaboo);
        patientService.saveOrUpdate(p);
        return "redirect:/patient";
    }
}

