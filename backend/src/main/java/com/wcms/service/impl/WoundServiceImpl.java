package com.wcms.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcms.common.utils.QiniuUtil;
import com.wcms.domain.dto.WoundResp;
import com.wcms.domain.entity.Patient;
import com.wcms.domain.entity.WoundRecord;
import com.wcms.event.EventPublisher;
import com.wcms.mapper.WoundRecordMapper;
import com.wcms.service.AIService;
import com.wcms.service.PatientService;
import com.wcms.service.WoundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WoundServiceImpl extends ServiceImpl<WoundRecordMapper, WoundRecord> implements WoundService {

    private final AIService aiService;
    private final PatientService patientService; // Inject PatientService
    private final QiniuUtil qiniuUtil;
    private final EventPublisher eventPublisher;

    @Override
    public List<WoundRecord> getWoundsByPatientId(Long patientId) {
        return this.list(new LambdaQueryWrapper<WoundRecord>().eq(WoundRecord::getPatientId, patientId).orderByDesc(WoundRecord::getCreateTime));
    }

    @Override
    public String uploadImage(MultipartFile file, Long patientId) {
        if (file.isEmpty()) throw new RuntimeException("Empty file");
        try {
            return qiniuUtil.uploadFile(file, patientId.toString());
        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WoundRecord submitAnalysis(Long patientId, String name, Integer age, Integer gender, String history, List<String> imageUrls, String medicalRecordNo) {
        // Update Patient
        Patient patient = patientService.getById(patientId);
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
    @Transactional(rollbackFor = Exception.class)
    public WoundRecord createAnalysisTask(Long patientId, String patientName, List<String> imageUrls, String history, String model, String medicalRecordNo) {
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
        eventPublisher.publishAiAnalysisEvent(record.getId());

        return record;
    }

    @Override
    public boolean retryAnalysis(Long recordId) {
        WoundRecord record = this.getById(recordId);
        if (record == null) return false;

        record.setStatus(0); // Reset to Pending
        record.setErrorMsg("");
        this.updateById(record);

        // Re-trigger Async AI
        aiService.analyzeWound(record.getId());
        return true;
    }

    @Override
    public IPage<WoundResp> getAdminWoundList(Page<WoundRecord> page) {
        // 1. Query Wound Records
        IPage<WoundRecord> recordPage = this.page(page, new LambdaQueryWrapper<WoundRecord>().orderByDesc(WoundRecord::getCreateTime));

        List<WoundResp> respList = new java.util.ArrayList<>();
        if (recordPage.getRecords().isEmpty()) {
            Page<WoundResp> result = new Page<>(page.getCurrent(), page.getSize());
            result.setTotal(recordPage.getTotal());
            result.setRecords(respList);
            return result;
        }

        // 2. Extract Patient IDs
        List<Long> patientIds = recordPage.getRecords().stream().map(WoundRecord::getPatientId).distinct().collect(Collectors.toList());

        // 3. Query Patients
        Map<Long, Patient> patientMap = patientService.listByIds(patientIds).stream().collect(Collectors.toMap(Patient::getId, p -> p));

        // 4. Merge
        for (WoundRecord r : recordPage.getRecords()) {
            WoundResp resp = new WoundResp();
            resp.setId(r.getId());
            resp.setPatientId(r.getPatientId());
            resp.setWoundType(r.getWoundType());
            resp.setImagePaths(r.getImagePaths());
            resp.setAnalysisResult(r.getAnalysisResult());
            resp.setAiModel(r.getAiModel());
            resp.setStatus(r.getStatus());
            resp.setErrorMsg(r.getErrorMsg());
            resp.setCreateTime(r.getCreateTime());

            Patient p = patientMap.get(r.getPatientId());
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

        Page<WoundResp> resultPage = new Page<>(page.getCurrent(), page.getSize());
        resultPage.setTotal(recordPage.getTotal());
        resultPage.setRecords(respList);
        return resultPage;
    }

    @Override
    public List<WoundRecord> getWoundTrend(Long patientId) {
        return this.list(new LambdaQueryWrapper<WoundRecord>().eq(WoundRecord::getPatientId, patientId).eq(WoundRecord::getStatus, 1) // Only successful analysis
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
