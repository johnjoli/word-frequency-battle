package com.bootcamp.repository;

import com.bootcamp.entity.WordCount;
import com.bootcamp.dto.WordCountSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WordCountRepository extends JpaRepository<WordCount, Long> {

    @Query("""
        SELECT new com.bootcamp.dto.WordCountSummary(
            w.word,
            SUM(w.count)
        )
        FROM WordCount w
        GROUP BY w.word
        ORDER BY SUM(w.count) DESC
        """)
    public List<WordCountSummary> findTopWords();

    @Query("SELECT COUNT(DISTINCT w.word) FROM WordCount w")
    long countUniqueWords();

    @Query("""
    SELECT new com.bootcamp.dto.WordCountSummary(
        w.word,
        SUM(w.count)
    )
    FROM WordCount w
    GROUP BY w.word
    ORDER BY SUM(w.count) DESC
    """)
    List<WordCountSummary> findTopWords(Pageable pageable);
}
