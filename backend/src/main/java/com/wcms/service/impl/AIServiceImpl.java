package com.wcms.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wcms.common.exception.ServiceException;
import com.wcms.common.utils.TipUtil;
import com.wcms.component.CallAI;
import com.wcms.constant.GenderEnum;
import com.wcms.domain.dto.AnalysisDTO;
import com.wcms.domain.entity.Patient;
import com.wcms.domain.entity.WoundRecord;
import com.wcms.mapper.WoundRecordMapper;
import com.wcms.service.AIService;
import com.wcms.service.PatientService;
import com.wcms.service.SysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final WoundRecordMapper woundRecordMapper;
    private final PatientService patientService;
    private final CallAI callAI;
    private final SysLogService sysLogService;

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

            String img;
            if (StringUtils.isEmpty(record.getImagePaths())) {
                log.error("改记录:{} , 图片为空，请核查！", recordId);
                throw new ServiceException("图片为空请核查！");
            } else {
                List<String> strings = JSON.parseArray(record.getImagePaths(), String.class);
                img = strings.get(0);
            }

            AnalysisDTO analysisDTO = callAI.call(img, tip);
            record.setWoundType(analysisDTO.getWoundType());
            record.setAnalysisResult(analysisDTO.getAnalysisResult());
            record.setStatus(1); // Success
            woundRecordMapper.updateById(record);

            log.info("Analysis Finished for Record: {}", recordId);
            sysLogService.log("AI",
                    "Analysis Finished for Record: " + recordId + ", Result: " + analysisDTO.getWoundType(), true);
        } catch (Exception e) {
            log.error("AI Analysis Failed", e);
            sysLogService.log("AI", "AI Analysis Failed for Record: " + recordId + ", Error: " + e.getMessage(), false);
            WoundRecord record = woundRecordMapper.selectById(recordId);
            if (record != null) {
                record.setStatus(2); // Fail
                record.setErrorMsg(e.getMessage());
                woundRecordMapper.updateById(record);
            }
        }
    }
}
