package com.wcms.event;

import com.wcms.domain.dto.AiAnalysisEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventPublisher {
    private final ApplicationEventPublisher publisher;

    public EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }


    public void publishAiAnalysisEvent(Long recordId) {
        log.info("Publishing AI analysis event for record ID: {}", recordId);
        publisher.publishEvent(new AiAnalysisEvent(recordId));
    }
}
