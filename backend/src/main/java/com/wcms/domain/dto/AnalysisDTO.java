package com.wcms.domain.dto;

import lombok.Data;

/**
 * @author brendan li
 */
@Data
public class AnalysisDTO {
    private String woundType;
    private String analysisResult;

    public AnalysisDTO() {
    }

    public AnalysisDTO(String woundType, String analysisResult) {
        this.woundType = woundType;
        this.analysisResult = analysisResult;
    }
}
