package com.wcms.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcms.domain.entity.WoundRecord;
import com.wcms.mapper.WoundRecordMapper;
import com.wcms.service.AIService;
import com.wcms.service.WoundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WoundServiceImpl extends ServiceImpl<WoundRecordMapper, WoundRecord> implements WoundService {

    private final AIService aiService;
    private final com.wcms.service.PatientService patientService; // Inject PatientService

    @Override
    public List<WoundRecord> getWoundsByPatientId(Long patientId) {
        return this.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WoundRecord>()
                .eq(WoundRecord::getPatientId, patientId)
                .orderByDesc(WoundRecord::getCreateTime));
    }

    @Override
    public String uploadImage(org.springframework.web.multipart.MultipartFile file) {
        if (file.isEmpty())
            throw new RuntimeException("Empty file");
        try {
            String fileName = java.util.UUID.randomUUID() + "-" + file.getOriginalFilename();
            String savePath = "d:/wcms/uploads/" + fileName;
            java.io.File dest = new java.io.File(savePath);
            if (!dest.getParentFile().exists())
                dest.getParentFile().mkdirs();
            file.transferTo(dest);
            return "/uploads/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }

    @Override
    public WoundRecord submitAnalysis(Long patientId, String name, Integer age, Integer gender, String history,
            List<String> imageUrls, String medicalRecordNo) {
        // Update Patient
        com.wcms.domain.entity.Patient patient = patientService.getById(patientId);
        if (patient != null) {
            patient.setName(name);
            patient.setAge(age);
            patient.setGender(gender);
            patient.setHistory(history);
            patientService.updateById(patient);
        }

        // Create Task
        return this.createAnalysisTask(patientId, name, imageUrls, history, "DeepSeek", medicalRecordNo);
    }

    @Override
    public WoundRecord createAnalysisTask(Long patientId, String patientName, List<String> imageUrls, String history,
            String model, String medicalRecordNo) {
        WoundRecord record = new WoundRecord();
        record.setPatientId(patientId);
        record.setPatientName(patientName);
        record.setImagePaths(JSON.toJSONString(imageUrls));
        record.setStatus(0); // Pending
        record.setAiModel(model);
        record.setMedicalRecordNo(medicalRecordNo);
        record.setCreateTime(LocalDateTime.now());

        this.save(record);

        // Async call
        aiService.analyzeWound(record.getId(), history, model);

        return record;
    }

    @Override
    public boolean retryAnalysis(Long recordId) {
        WoundRecord record = this.getById(recordId);
        if (record == null)
            return false;

        record.setStatus(0); // Reset to Pending
        record.setErrorMsg("");
        this.updateById(record);

        // Re-trigger Async AI
        aiService.analyzeWound(record.getId(), "", record.getAiModel());
        return true;
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<com.wcms.domain.dto.WoundResp> getAdminWoundList(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<WoundRecord> page) {
        // 1. Query Wound Records
        com.baomidou.mybatisplus.core.metadata.IPage<WoundRecord> recordPage = this.page(page,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WoundRecord>()
                        .orderByDesc(WoundRecord::getCreateTime));

        List<com.wcms.domain.dto.WoundResp> respList = new java.util.ArrayList<>();
        if (recordPage.getRecords().isEmpty()) {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.wcms.domain.dto.WoundResp> result = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                    page.getCurrent(), page.getSize());
            result.setTotal(recordPage.getTotal());
            result.setRecords(respList);
            return result;
        }

        // 2. Extract Patient IDs
        List<Long> patientIds = recordPage.getRecords().stream()
                .map(WoundRecord::getPatientId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        // 3. Query Patients
        java.util.Map<Long, com.wcms.domain.entity.Patient> patientMap = patientService.listByIds(patientIds).stream()
                .collect(java.util.stream.Collectors.toMap(com.wcms.domain.entity.Patient::getId, p -> p));

        // 4. Merge
        for (WoundRecord r : recordPage.getRecords()) {
            com.wcms.domain.dto.WoundResp resp = new com.wcms.domain.dto.WoundResp();
            resp.setId(r.getId());
            resp.setPatientId(r.getPatientId());
            resp.setWoundType(r.getWoundType());
            resp.setImagePaths(r.getImagePaths());
            resp.setAnalysisResult(r.getAnalysisResult());
            resp.setAiModel(r.getAiModel());
            resp.setStatus(r.getStatus());
            resp.setErrorMsg(r.getErrorMsg());
            resp.setCreateTime(r.getCreateTime());

            com.wcms.domain.entity.Patient p = patientMap.get(r.getPatientId());
            if (p != null) {
                resp.setPatientName(p.getName());
                resp.setPatientAge(p.getAge());
                resp.setPatientGender(p.getGender());
                resp.setPatientPhone(p.getPhone());
            } else {
                resp.setPatientName(r.getPatientName());
            }
            respList.add(resp);
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.wcms.domain.dto.WoundResp> resultPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                page.getCurrent(), page.getSize());
        resultPage.setTotal(recordPage.getTotal());
        resultPage.setRecords(respList);
        return resultPage;
    }

    @Override
    public List<WoundRecord> getWoundTrend(Long patientId) {
        return this.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WoundRecord>()
                .eq(WoundRecord::getPatientId, patientId)
                .eq(WoundRecord::getStatus, 1) // Only successful analysis
                .orderByAsc(WoundRecord::getCreateTime));
    }

    @Override
    public boolean updateEvaluation(Long id, String evaluation) {
        WoundRecord record = new WoundRecord();
        record.setId(id);
        record.setDoctorEvaluation(evaluation);
        return this.updateById(record);
    }
}
