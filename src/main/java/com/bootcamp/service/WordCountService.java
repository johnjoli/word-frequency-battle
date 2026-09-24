package com.bootcamp.service;

import com.bootcamp.dto.StatsResponse;
import com.bootcamp.dto.WordCountResultDto;
import com.bootcamp.dto.WordCountResultSummaryDto;
import com.bootcamp.entity.WordCountResult;
import com.bootcamp.mapper.WordCountResultMapper;
import com.bootcamp.repository.WordCountResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WordCountService {

    private final WordCounter wordCounter;
    private final WordCountResultRepository repository;
    private final WordCountResultMapper mapper;

    @Autowired
    public WordCountService(WordCounter wordCounter, WordCountResultRepository repository, WordCountResultMapper mapper) {
        this.wordCounter = wordCounter;
        this.repository = repository;
        this.mapper = mapper;
    }

    public WordCountResult processFile(Path filePath, String originalFileName) throws IOException {
        Map<String, Long> counts = wordCounter.countWords(filePath);
        WordCountResult result = new WordCountResult(originalFileName, counts);
        return repository.save(result);
    }

    public Map<String, Long> getTopWords(int limit) {
        List<WordCountResult> allResults = repository.findAll();

        Map<String, Long> totalCounts = new HashMap<>();
        for (WordCountResult result : allResults) {
            for (Map.Entry<String, Long> entry : result.getWordCounts().entrySet()) {
                totalCounts.merge(entry.getKey(), entry.getValue(),Long::sum);
            }
        }
        return totalCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(LinkedHashMap::new,
                        (map, e) -> map.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll);
    }

    public StatsResponse getStats() {

        List<WordCountResult> allResults = repository.findAll();

        long totalFiles = allResults.size();

        Map<String, Long> totalCounts = new HashMap<>();
        for (WordCountResult result : allResults) {
            for (Map.Entry<String, Long> entry : result.getWordCounts().entrySet()) {
                totalCounts.merge(entry.getKey(), entry.getValue(), Long::sum);
            }
        }

        int uniqueWords = totalCounts.size();

        String topWord= null;
        long topWordCount = 0;
        for (Map.Entry<String, Long> entry : totalCounts.entrySet()) {
            if (entry.getValue() > topWordCount) {
                topWord = entry.getKey();
                topWordCount = entry.getValue();
            }
        }
        return new StatsResponse(totalFiles, uniqueWords, topWord, topWordCount);
    }

    public Page<WordCountResultSummaryDto> getHistory(String fileName, LocalDateTime from,
                                                      LocalDateTime to, Pageable pageable) {
       return repository.findWithFilters(fileName, from, to, pageable)
               .map(mapper::toSummaryDto);
    }

    public WordCountResultDto getResultById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    public boolean deleteResult(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
