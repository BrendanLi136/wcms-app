package com.wcms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

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

