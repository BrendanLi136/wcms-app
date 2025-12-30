package com.wcms.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcms.common.result.Result;
import com.wcms.domain.entity.Reminder;
import com.wcms.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/reminder")
@RequiredArgsConstructor
public class AdminReminderController {

    private final ReminderService reminderService;

    @GetMapping("/patient/{patientId}")
    public Result<List<Reminder>> listByPatient(@PathVariable Long patientId) {
        return Result.success(reminderService.list(new LambdaQueryWrapper<Reminder>()
                .eq(Reminder::getPatientId, patientId)
                .eq(Reminder::getIsActive, true)));
    }

    @PostMapping("/create")
    public Result<Boolean> create(@RequestBody Reminder reminder) {
        reminder.setIsActive(true);
        reminder.setCreateTime(LocalDateTime.now());
        // Default next run to today if not set, or let scheduler handle.
        // Usually, set nextRunDate to today.
        if (reminder.getNextRunDate() == null) {
            reminder.setNextRunDate(java.time.LocalDate.now());
        }
        return Result.success(reminderService.save(reminder));
    }

    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(reminderService.removeById(id));
    }
}
