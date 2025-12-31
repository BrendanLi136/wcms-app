package com.wcms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志表
 */
@Data
@TableName("sys_log")
public class SysLog {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 日志类型: AI / SMS
     */
    private String type;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 状态: 0:Fail, 1:Success
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
