package com.wcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WoundResp {
    private Long id;
    private Long patientId;

    // Patient Info (Real-time)
    private String patientName;
    private Integer patientAge;
    private Integer patientGender;
    private String patientPhone;

    // Wound Info
    private String woundType;
    private String imagePaths;
    private String analysisResult;
    private String aiModel;
    private Integer status;
    private String errorMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
