package com.wcms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcms.domain.entity.Reminder;

public interface ReminderService extends IService<Reminder> {
    void processDailyReminders();
}
