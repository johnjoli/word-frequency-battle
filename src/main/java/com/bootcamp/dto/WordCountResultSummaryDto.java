package com.bootcamp.dto;

import java.time.LocalDateTime;

public class WordCountResultSummaryDto {

    private Long id;
    private String fileName;
    private LocalDateTime processedAt;
    private int uniqueWordsCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public int getUniqueWordsCount() { return uniqueWordsCount; }
    public void setUniqueWordsCount(int uniqueWordsCount) { this.uniqueWordsCount = uniqueWordsCount; }
}