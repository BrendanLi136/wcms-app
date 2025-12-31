package com.wcms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcms.domain.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志 Mapper 接口
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {
}
