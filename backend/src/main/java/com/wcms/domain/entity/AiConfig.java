package com.wcms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_config")
public class AiConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String modelUrl;
    private String modelType;
    private String apiKey;
    private String remark;
    private Integer isActive; // 1:Active, 0:Inactive
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
