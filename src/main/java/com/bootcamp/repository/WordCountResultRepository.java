package com.bootcamp.repository;

import com.bootcamp.entity.WordCountResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordCountResultRepository extends JpaRepository<WordCountResult, Long> {
}
