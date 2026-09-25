package com.bootcamp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "word_count_results")
public class WordCountResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @ElementCollection
    @CollectionTable(name = "word_counts",
    joinColumns = @JoinColumn(name = "result_id"))
    @MapKeyColumn(name = "word")
    @Column(name = "count")
    private Map<String, Long> wordCounts;

    public WordCountResult() {
    }

    public WordCountResult(String fileName, Map<String, Long> wordCounts) {
        this.fileName = fileName;
        this.wordCounts = wordCounts;
        this.processedAt = LocalDateTime.now();
    }

    public String getFileName() {
        return fileName;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public Map<String, Long> getWordCounts() {
        return wordCounts;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public void setWordCounts(Map<String, Long> wordCounts) {
        this.wordCounts = wordCounts;
    }

    public Long getVersion() { return version; }

    public void setVersion(Long version) { this.version = version; }
}
