package com.wcms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcms.domain.entity.AiConfig;

public interface AiConfigService extends IService<AiConfig> {
    AiConfig getActiveConfig();
}
