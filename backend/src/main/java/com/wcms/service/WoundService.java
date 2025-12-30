package com.wcms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wcms.domain.dto.WoundResp;
import com.wcms.domain.entity.WoundRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WoundService extends IService<WoundRecord> {
    // Basic CRUD is inherited
    // Analyze Logic
    WoundRecord createAnalysisTask(Long patientId, String patientName, List<String> imageUrls, String history,
                                   String model, String medicalRecordNo);

    List<WoundRecord> getWoundsByPatientId(Long patientId);

    String uploadImage(MultipartFile file, Long patientId);

    WoundRecord submitAnalysis(Long patientId, String name, Integer age, Integer gender, String history,
                               List<String> imageUrls, String medicalRecordNo);

    boolean retryAnalysis(Long recordId);

    IPage<WoundResp> getAdminWoundList(Page<WoundRecord> page);

    List<WoundRecord> getWoundTrend(Long patientId);

    boolean updateEvaluation(Long id, String evaluation);
}
