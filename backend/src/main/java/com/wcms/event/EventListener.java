package com.wcms.event;

import com.wcms.domain.dto.AiAnalysisEvent;
import com.wcms.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class EventListener {

    @Autowired
    private AIService aiService;


    @TransactionalEventListener(value = AiAnalysisEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void handleAiAnalysisEvent(AiAnalysisEvent event) {
        Long recordId = event.getRecordId();
        // 处理AI分析事件，例如触发分析任务
        log.info("Handling AI analysis event for record ID: {}", recordId);
        aiService.analyzeWound(recordId);
    }
}
