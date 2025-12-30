package com.wcms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcms.domain.entity.Patient;
import com.wcms.domain.entity.Reminder;
import com.wcms.domain.entity.ReminderLog;
import com.wcms.mapper.ReminderMapper;
import com.wcms.service.ReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ReminderServiceImpl extends ServiceImpl<ReminderMapper, Reminder> implements ReminderService {
    private final ReminderMapper reminderMapper;
    private final com.wcms.mapper.ReminderLogMapper reminderLogMapper;
    private final com.wcms.service.PatientService patientService;

    public ReminderServiceImpl(ReminderMapper reminderMapper, com.wcms.mapper.ReminderLogMapper reminderLogMapper, com.wcms.service.PatientService patientService) {
        this.reminderMapper = reminderMapper;
        this.reminderLogMapper = reminderLogMapper;
        this.patientService = patientService;
    }

    @Override
    @Scheduled(cron = "0 0/1 * * * ?") // Check every minute
    public void processDailyReminders() {
        log.info("Checking for reminders... {}", LocalDateTime.now());

        // Find active reminders where nextRunDate <= today AND remindTime <= now
        // Simplified Logic: Select all active, filter in memory for prototype
        List<Reminder> reminders = this.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Reminder>().eq(Reminder::getIsActive, true));

        LocalDateTime now = LocalDateTime.now();

        for (Reminder r : reminders) {
            // Check if due
            // For checking 'Time', we need careful comparison.
            // If nextRunDate is today (or past) AND current time >= remindTime
            if (r.getNextRunDate() != null && (r.getNextRunDate().isBefore(now.toLocalDate()) || r.getNextRunDate().isEqual(now.toLocalDate())) && (now.toLocalTime().isAfter(r.getRemindTime()) || now.toLocalTime().equals(r.getRemindTime()))) {

                // Trigger Reminder
                log.info("Triggering Reminder for patientId: {}, Content: {}", r.getPatientId(), r.getContent());
                sendSms(r);

                // Update Next Run Date
                updateNextRunDate(r);
            }
        }
    }

    private void sendSms(Reminder r) {
        log.info("Sending SMS to patientId: {}, Content: {}", r.getPatientId(), r.getContent());
        // Mock Send SMS
        boolean success = true;

        // Get Patient Phone
        Patient p = patientService.getById(r.getPatientId());
        String phone = (p != null) ? p.getPhone() : "Unknown";

        // Save Log
        ReminderLog log = new ReminderLog();
        log.setPatientId(r.getPatientId());
        log.setPhone(phone);
        log.setContent(r.getContent());
        log.setStatus(success ? 1 : 0);
        log.setCreateTime(LocalDateTime.now());
        reminderLogMapper.insert(log);
    }

    private void updateNextRunDate(Reminder r) {
        // Frequency: 1:Daily, 2:Every 2 Days
        int daysToAdd = (r.getFrequency() != null && r.getFrequency() == 2) ? 2 : 1;
        r.setNextRunDate(java.time.LocalDate.now().plusDays(daysToAdd));
        this.updateById(r);
    }
}