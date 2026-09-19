package com.bootcamp.service;

import com.bootcamp.entity.WordCountResult;
import com.bootcamp.repository.WordCountResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Service
@Transactional
public class WordCountService {

    private final WordCounter wordCounter;
    private final WordCountResultRepository repository;

    @Autowired
    public WordCountService(WordCounter wordCounter, WordCountResultRepository repository) {
        this.wordCounter = wordCounter;
        this.repository = repository;
    }

    public WordCountResult processFile(Path filePath, String originalFileName) throws IOException {
        Map<String, Long> counts = wordCounter.countWords(filePath);
        WordCountResult result = new WordCountResult(originalFileName, counts);
        return repository.save(result);
    }
}
