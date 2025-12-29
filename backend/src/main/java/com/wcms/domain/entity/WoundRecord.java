package com.wcms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wound_record")
public class WoundRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private String imagePaths; // JSON or comma separated
    private String analysisResult;
    private String aiModel;
    private Integer status; // 0:Pending, 1:Success, 2:Fail
    private String errorMsg;
    private LocalDateTime createTime;

    private String patientName;
    private String woundType;
    private String doctorEvaluation;
    private String medicalRecordNo;
}
