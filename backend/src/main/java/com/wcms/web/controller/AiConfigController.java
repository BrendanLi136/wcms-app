package com.wcms.web.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wcms.common.result.Result;
import com.wcms.domain.entity.AiConfig;
import com.wcms.service.AiConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/ai-config")
@RequiredArgsConstructor
public class AiConfigController {

    private final AiConfigService aiConfigService;

    @GetMapping("/list")
    public Result<List<AiConfig>> list() {
        return Result.success(aiConfigService.list());
    }

    @PostMapping("/save")
    public Result<Boolean> save(@RequestBody AiConfig config) {
        // Simple logic: if setting to active, deactivate others
        if (config.getIsActive() != null && config.getIsActive() == 1) {
            aiConfigService.update(Wrappers.<AiConfig>lambdaUpdate().set(AiConfig::getIsActive, 0));
        }
        return Result.success(aiConfigService.saveOrUpdate(config));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(aiConfigService.removeById(id));
    }
}
