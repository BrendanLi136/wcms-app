package com.wcms.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiAnalysisEvent {
    private Long recordId;

    public AiAnalysisEvent(Long recordId) {
        this.recordId = recordId;
    }
}
