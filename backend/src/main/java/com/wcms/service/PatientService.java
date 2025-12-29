package com.wcms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcms.domain.entity.Patient;

public interface PatientService extends IService<Patient> {
    Patient getByOpenId(String openid);

    Patient loginByCode(String code);

    Patient updatePhone(Long userId, String code, String encryptedData, String iv);

    com.baomidou.mybatisplus.core.metadata.IPage<Patient> getPatientPage(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<Patient> page, String name);
}

// Separate WoundService.java
/*
 * package com.wcms.service;
 * public interface WoundService extends IService<WoundRecord> {
 * void uploadAndAnalyze(Long patientId, List<String> files, String model);
 * }
 */
