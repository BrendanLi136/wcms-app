package com.wcms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcms.domain.entity.AiConfig;
import com.wcms.mapper.AiConfigMapper;
import com.wcms.service.AiConfigService;
import org.springframework.stereotype.Service;

@Service
public class AiConfigServiceImpl extends ServiceImpl<AiConfigMapper, AiConfig> implements AiConfigService {

    @Override
    public AiConfig getActiveConfig() {
        return this.getOne(new LambdaQueryWrapper<AiConfig>()
                .eq(AiConfig::getIsActive, 1)
                .last("LIMIT 1"));
    }
}
