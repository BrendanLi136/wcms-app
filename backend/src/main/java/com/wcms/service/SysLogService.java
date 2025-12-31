package com.wcms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcms.domain.entity.SysLog;

/**
 * 系统日志 Service 接口
 */
public interface SysLogService extends IService<SysLog> {
    /**
     * 保存日志
     * 
     * @param type    日志类型: AI / SMS
     * @param content 日志内容
     * @param success 是否成功
     */
    void log(String type, String content, boolean success);
}
