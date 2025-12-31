package com.wcms.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcms.common.result.Result;
import com.wcms.domain.entity.Patient;
import com.wcms.domain.entity.WoundRecord;
import com.wcms.service.PatientService;
import com.wcms.service.WoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final WoundService woundService;

    @GetMapping("/list")
    public Result<Page<Patient>> list(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        Page<Patient> pageParam = new Page<>(page, size);
        return Result.success((Page<Patient>) patientService.getPatientPage(pageParam, name));
    }

    @GetMapping("/{id}")
    public Result<Patient> get(@PathVariable Long id) {
        return Result.success(patientService.getById(id));
    }

    @GetMapping("/{id}/wounds")
    public Result<List<WoundRecord>> getWounds(@PathVariable Long id) {
        return Result.success(woundService.getWoundsByPatientId(id));
    }
}
