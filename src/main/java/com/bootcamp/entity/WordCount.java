package com.bootcamp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "word_counts")
public class WordCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "result_id")
    private WordCountResult result;

    @Column(name = "word")
    private String word;

    @Column(name = "count")
    private long count;

    protected WordCount() {
    }

    public WordCount(String word, long count) {
        this.word = word;
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WordCountResult getResult() {
        return result;
    }

    public void setResult(WordCountResult result) {
        this.result = result;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
