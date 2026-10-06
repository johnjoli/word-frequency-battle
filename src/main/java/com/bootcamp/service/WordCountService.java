package com.bootcamp.service;

import com.bootcamp.dto.StatsResponse;
import com.bootcamp.dto.WordCountResultDto;
import com.bootcamp.dto.WordCountResultSummaryDto;
import com.bootcamp.dto.WordCountSummary;
import com.bootcamp.entity.WordCount;
import com.bootcamp.entity.WordCountResult;
import com.bootcamp.mapper.WordCountResultMapper;
import com.bootcamp.repository.WordCountRepository;
import com.bootcamp.repository.WordCountResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final WordCountRepository wordCountRepository;

    @Autowired
    public WordCountService(WordCounter wordCounter,
                            WordCountResultRepository repository,
                            WordCountResultMapper mapper,
                            WordCountRepository wordCountRepository) {
        this.wordCounter = wordCounter;
        this.repository = repository;
        this.mapper = mapper;
        this.wordCountRepository = wordCountRepository;
    }

    public WordCountResult processFile(Path filePath, String originalFileName) throws IOException {
        Map<String, Long> counts = wordCounter.countWords(filePath);

        WordCountResult result = new WordCountResult(originalFileName);
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            WordCount wordCount = new WordCount(entry.getKey(), entry.getValue());
            result.addWordCount(wordCount);
        }
        return repository.save(result);
    }

    public Map<String, Long> getTopWords(int limit) {
        return wordCountRepository.findTopWords()
                .stream()
                .limit(limit)
                .collect(Collectors.toMap(
                        WordCountSummary::word,
                        WordCountSummary::count,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public StatsResponse getStats() {
        long totalFiles = repository.count();
        long uniqueWords = wordCountRepository.countUniqueWords();

        List<WordCountSummary> topWords =
                wordCountRepository.findTopWords(PageRequest.of(0, 1));

        Optional<WordCountSummary> topWord =
                topWords.stream().findFirst();

        String mostFrequentWord =
                topWord.map(WordCountSummary::word).orElse(null);

        long mostFrequentWordCount =
                topWord.map(WordCountSummary::count).orElse(0L);

        return new StatsResponse(
                totalFiles,
                (int) uniqueWords,
                mostFrequentWord,
                mostFrequentWordCount
        );
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
