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
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

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

    @PostMapping("/updatePhone")
    public Result<Patient> updatePhone(@RequestBody PhoneReq req) {
        Patient patient = patientService.updatePhone(req.getUserId(), req.getCode(), req.getEncryptedData(),
                req.getIv());
        if (patient != null) {
            return Result.success(patient);
        }
        return Result.error("User not found or Decrypt failed");
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
    public static class PhoneReq {
        private Long userId;
        private String code; // can be removed if using sessionKey stored in backend, but often frontend
                             // sends code to refresh session if needed.
                             // However, for decryption we need session_key.
                             // Simplify: Frontend sends code -> Backend gets OpenID+SessionKey.
                             // OR Frontend sends encryptedData + iv + code (to get sessionKey).
        private String encryptedData;
        private String iv;
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
