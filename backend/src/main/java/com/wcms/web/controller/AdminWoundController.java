package com.wcms.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcms.common.result.Result;
import com.wcms.domain.entity.Patient;
import com.wcms.domain.entity.WoundRecord;
import com.wcms.service.PatientService;
import com.wcms.service.WoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/wound")
@RequiredArgsConstructor
public class AdminWoundController {

    private final WoundService woundService;
    private final PatientService patientService;

    @GetMapping("/list")
    public Result<IPage<com.wcms.domain.dto.WoundResp>> list(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<WoundRecord> pageParam = new Page<>(page, size);
        return Result.success(woundService.getAdminWoundList(pageParam));
    }

    @PostMapping("/{id}/retry")
    public Result<Boolean> retry(@PathVariable Long id) {
        return Result.success(woundService.retryAnalysis(id));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody WoundRecord wound) {
        wound.setId(id);
        return Result.success(woundService.updateById(wound));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(woundService.removeById(id));
    }

    @GetMapping("/trend/{patientId}")
    public Result<List<WoundRecord>> getTrend(@PathVariable Long patientId) {
        return Result.success(woundService.getWoundTrend(patientId));
    }

    @PostMapping("/{id}/evaluation")
    public Result<Boolean> evaluation(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success(woundService.updateEvaluation(id, body.get("evaluation")));
    }
}
