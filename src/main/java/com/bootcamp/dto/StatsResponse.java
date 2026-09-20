package com.bootcamp.dto;

public class StatsResponse {

    private long totalFilesProcessed;
    private int totalUniqueWords;
    private String mostFrequentWord;
    private long mostFrequentWordCount;

    public StatsResponse(long totalFilesProcessed, int totalUniqueWords,
                         String mostFrequentWord, long mostFrequentWordCount) {
        this.totalFilesProcessed = totalFilesProcessed;
        this.totalUniqueWords = totalUniqueWords;
        this.mostFrequentWord = mostFrequentWord;
        this.mostFrequentWordCount = mostFrequentWordCount;
    }

    public String getMostFrequentWord() {
        return mostFrequentWord;
    }

    public long getMostFrequentWordCount() {
        return mostFrequentWordCount;
    }

    public long getTotalFilesProcessed() {
        return totalFilesProcessed;
    }

    public int getTotalUniqueWords() {
        return totalUniqueWords;
    }
}
