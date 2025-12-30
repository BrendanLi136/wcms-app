package com.wcms.service.impl;

import com.wcms.common.utils.TipUtil;
import com.wcms.component.CallAI;
import com.wcms.constant.GenderEnum;
import com.wcms.domain.dto.AnalysisDTO;
import com.wcms.domain.entity.Patient;
import com.wcms.domain.entity.WoundRecord;
import com.wcms.mapper.WoundRecordMapper;
import com.wcms.service.AIService;
import com.wcms.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final WoundRecordMapper woundRecordMapper;
    private final PatientService patientService;
    private final CallAI callAI;



    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void analyzeWound(Long recordId) {
        try {
            WoundRecord record = woundRecordMapper.selectById(recordId);
            if (record == null)
                return;

            Patient patient = patientService.getById(record.getPatientId());
            if (patient == null)
                return;

            String tip = TipUtil.getTip(String.valueOf(patient.getAge()), GenderEnum.fromCode(patient.getGender()),
                    patient.getHistory());
            log.info("Starting Async Analysis for Record: {}, URL: {}", recordId, record.getImagePaths());

            AnalysisDTO analysisDTO = callAI.call(record.getImagePaths(), tip);
            record.setWoundType(analysisDTO.getWoundType());
            record.setAnalysisResult(analysisDTO.getAnalysisResult());
            record.setStatus(1); // Success
            woundRecordMapper.updateById(record);

            log.info("Analysis Finished for Record: {}", recordId);
        } catch (Exception e) {
            log.error("AI Analysis Failed", e);
            WoundRecord record = woundRecordMapper.selectById(recordId);
            if (record != null) {
                record.setStatus(2); // Fail
                record.setErrorMsg(e.getMessage());
                woundRecordMapper.updateById(record);
            }
        }
    }
}
