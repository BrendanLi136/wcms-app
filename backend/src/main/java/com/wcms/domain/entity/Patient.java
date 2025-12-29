package com.wcms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("patient")
public class Patient {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private String phone;
    private String name;
    private Integer gender; // 1:Male, 2:Female
    private Integer age;
    private String history;
    private LocalDateTime createTime;
}

// Separate file WoundRecord.java
/*
 * package com.wcms.domain.entity;
 * // ... imports
 * 
 * @Data
 * 
 * @TableName("wound_record")
 * public class WoundRecord {
 * 
 * @TableId(type = IdType.AUTO)
 * private Long id;
 * private Long patientId;
 * private String imagePaths; // JSON or comma separated
 * private String analysisResult;
 * private String aiModel;
 * private Integer status; // 0:Pending, 1:Success, 2:Fail
 * private String errorMsg;
 * private LocalDateTime createTime;
 * }
 */

// Separate file Reminder.java
/*
 * package com.wcms.domain.entity;
 * // ... imports
 * 
 * @Data
 * 
 * @TableName("reminder")
 * public class Reminder {
 * 
 * @TableId(type = IdType.AUTO)
 * private Long id;
 * private Long patientId;
 * private LocalTime remindTime;
 * private Integer frequency; // 1:Daily, 2:Separate Day, 3:Once
 * private LocalDate nextRunDate;
 * private String content;
 * private Boolean isActive;
 * private LocalDateTime createTime;
 * }
 */
