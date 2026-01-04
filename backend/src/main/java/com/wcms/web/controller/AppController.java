package com.wcms.web.controller;

import com.wcms.common.result.Result;
import com.wcms.domain.entity.Patient;
import com.wcms.service.PatientService;
import com.wcms.service.WoundService;
import com.wcms.domain.entity.WoundRecord;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppController {

    private final PatientService patientService;
    private final WoundService woundService;

    // Login (Mock Wechat Login)
    @PostMapping("/login")
    public Result<Patient> login(@RequestBody LoginReq req) {
        return Result.success(patientService.loginByCode(req.getCode()));
    }

    @PostMapping("/updateProfile")
    public Result<Patient> updateProfile(@RequestBody UpdateProfileReq req) {
        Patient patient = patientService.getById(req.getUserId());
        if (patient == null) {
            return Result.error("User not found");
        }
//        patient.setName(req.getName());
        patient.setPhone(req.getPhone());
//        patient.setGender(req.getGender());
//        patient.setAge(req.getAge());
//        patient.setHistory(req.getHistory());
        patientService.updateById(patient);
        return Result.success(patient);
    }

    // Upload Image
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file, @RequestParam("patientId") Long patientId) {
        return Result.success(woundService.uploadImage(file, patientId));
    }

    // Submit Analysis
    @PostMapping("/analysis/submit")
    public Result<WoundRecord> submitAnalysis(@RequestBody AnalysisReq req) {
        WoundRecord record = woundService.submitAnalysis(
                req.getPatientId(),
                req.getName(),
                req.getAge(),
                req.getGender(),
                req.getHistory(),
                req.getImageUrls(),
                req.getMedicalRecordNo());
        return Result.success(record);
    }

    @GetMapping("/history")
    public Result<List<WoundRecord>> getHistory(@RequestParam Long patientId) {
        return Result.success(woundService.getWoundsByPatientId(patientId));
    }

    private final com.wcms.mapper.ReminderLogMapper reminderLogMapper;

    @GetMapping("/reminders")
    public Result<List<com.wcms.domain.entity.ReminderLog>> getReminders(@RequestParam Long patientId) {
        return Result.success(reminderLogMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.wcms.domain.entity.ReminderLog>()
                        .eq(com.wcms.domain.entity.ReminderLog::getPatientId, patientId)
                        .orderByDesc(com.wcms.domain.entity.ReminderLog::getCreateTime)));
    }

    @Data
    public static class LoginReq {
        private String code;
    }

    @Data
    public static class UpdateProfileReq {
        private Long userId;
        private String name;
        private String phone;
        private Integer gender;
        private Integer age;
        private String history;
    }

    @Data
    public static class AnalysisReq {
        private Long patientId;
        private String name;
        private Integer gender;
        private Integer age;
        private String history;
        private List<String> imageUrls;
        private String medicalRecordNo;
    }
}
