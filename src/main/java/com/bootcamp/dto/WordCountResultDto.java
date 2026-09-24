package com.bootcamp.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class WordCountResultDto {

    private Long id;
    private String fileName;
    private LocalDateTime processedAt;
    private Map<String, Long> wordCounts;

    // геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public Map<String, Long> getWordCounts() { return wordCounts; }
    public void setWordCounts(Map<String, Long> wordCounts) { this.wordCounts = wordCounts; }
}