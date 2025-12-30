package com.wcms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reminder_log")
public class ReminderLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private String phone;
    private String content;
    private Integer status; // 1:Success, 0:Fail
    private LocalDateTime createTime;
}
