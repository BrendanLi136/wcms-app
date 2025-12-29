package com.wcms.service;

public interface AIService {
    void analyzeWound(Long recordId, String history, String modelName);
}
