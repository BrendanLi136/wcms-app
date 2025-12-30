package com.wcms.service.impl;

import com.wcms.common.utils.TipUtil;
import com.wcms.constant.GenderEnum;
import com.wcms.domain.entity.Patient;
import com.wcms.domain.entity.WoundRecord;
import com.wcms.mapper.WoundRecordMapper;
import com.wcms.service.AIService;
import com.wcms.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    @Value("${wcms.ai.model-name}")
    private String modelName;

    @Value("${wcms.ai.access-key}")
    private String accessKey;

    private final WoundRecordMapper woundRecordMapper;
    private final PatientService patientService;

    @Async
    @Override
    public void analyzeWound(Long recordId) {
        log.info("Starting Async Analysis for Record: {}, Model: {}", recordId, modelName);
        try {
            // Mocking Processing Time
            TimeUnit.SECONDS.sleep(3);


            WoundRecord record = woundRecordMapper.selectById(recordId);
            if (record == null)
                return;

            Patient patient = patientService.getById(record.getPatientId());
            if (patient == null)
                return;

            String tip = TipUtil.getTip(String.valueOf(patient.getAge()), GenderEnum.fromCode(patient.getGender()), patient.getHistory());



            // Mock Result based on Model
            String result = "Mock Analysis Result from " + modelName + ": \n" +
                    "伤口类型: 创伤性溃疡 \n" +
                    "面积估计: 4.5 cm2 \n" +
                    "愈合建议: 保持干燥，每日换药。";

//            if (history != null && history.contains("糖尿病")) {
//                result += "\n注意: 患者有糖尿病史，愈合可能较慢，建议严密观察。";
//            }

            record.setAnalysisResult(result);
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
