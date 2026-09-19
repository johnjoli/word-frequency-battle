package com.bootcamp.repository;

import com.bootcamp.entity.WordCountResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface WordCountResultRepository extends JpaRepository<WordCountResult, Long> {

    @Query("SELECT w FROM WordCountResult w WHERE " +
    "(:fileName IS NULL OR w.fileName LIKE CONCAT('%', :fileName, '%')) AND " +
    "(:from IS NULL OR w.processedAt >= :from) AND " +
    "(: to IS NULL OR w.processedAt <= :to)")
    Page<WordCountResult> findWithFilters(@Param("fileName") String fileName,
                                          @Param("from")LocalDateTime from,
                                          @Param("to") LocalDateTime to,
                                          Pageable pageable);

}
