package com.wcms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcms.domain.entity.SysLog;
import com.wcms.mapper.SysLogMapper;
import com.wcms.service.SysLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 系统日志 Service 实现类
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public void log(String type, String content, boolean success) {
        SysLog sysLog = new SysLog();
        sysLog.setType(type);
        sysLog.setContent(content);
        sysLog.setStatus(success ? 1 : 0);
        sysLog.setCreateTime(LocalDateTime.now());
        this.save(sysLog);
    }
}
