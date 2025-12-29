package com.wcms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("reminder")
public class Reminder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private LocalTime remindTime;
    private Integer frequency; // 1:Daily, 2:Every 2 Days, 3:Once
    private LocalDate nextRunDate;
    private String content;
    private Boolean isActive;
    private LocalDateTime createTime;
}
