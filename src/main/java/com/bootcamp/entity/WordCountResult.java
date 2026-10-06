package com.bootcamp.entity;

import com.bootcamp.service.WordCounter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    @OneToMany(
            mappedBy = "result",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WordCount> wordCounts = new ArrayList<>();

    public WordCountResult() {
    }

    public WordCountResult(String fileName) {
        this.fileName = fileName;
        this.processedAt = LocalDateTime.now();
    }

    public void addWordCount(WordCount wordCount) {
        wordCounts.add(wordCount);
        wordCount.setResult(this);
    }

    public void removeWordCount(WordCount wordCount) {
        wordCounts.remove(wordCount);
        wordCount.setResult(null);
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

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public Long getVersion() { return version; }

    public void setVersion(Long version) { this.version = version; }

    public List<WordCount> getWordCounts() {
        return wordCounts;
    }

    public void setWordCounts(List<WordCount> wordCounts) {
        this.wordCounts = wordCounts;
    }
}
