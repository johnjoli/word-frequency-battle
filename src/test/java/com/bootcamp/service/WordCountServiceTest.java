package com.bootcamp.service;

import com.bootcamp.config.WordCounterProperties;
import com.bootcamp.dto.StatsResponse;
import com.bootcamp.dto.WordCountSummary;
import com.bootcamp.entity.WordCount;
import com.bootcamp.entity.WordCountResult;
import com.bootcamp.mapper.WordCountResultMapperImpl;
import com.bootcamp.repository.WordCountRepository;
import com.bootcamp.repository.WordCountResultRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({WordCountService.class, WordCounter.class, WordCountResultMapperImpl.class})
@ActiveProfiles("test")
class WordCountServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withReuse(true);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WordCountService wordCountService;

    @Autowired
    private WordCountResultRepository repository;

    @Autowired
    private WordCountRepository wordCountRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        WordCounterProperties wordCounterProperties() {
            return new WordCounterProperties();
        }
    }

    @Test
    void processFile_shouldSaveResultToDatabase() throws Exception {
        Path file = Files.createTempFile("test", ".txt");
        Files.writeString(file, "Java, java! Spring?");

        WordCountResult result = wordCountService.processFile(file, "test.txt");

        assertNotNull(result.getId());
        assertEquals("test.txt", result.getFileName());
        assertEquals(2L,
                result.getWordCounts().stream()
                        .filter(w -> w.getWord().equals("java"))
                        .findFirst()
                        .orElseThrow()
                        .getCount());
        assertEquals(1L,
                result.getWordCounts().stream()
                        .filter(w -> w.getWord().equals("spring"))
                        .findFirst()
                        .orElseThrow()
                        .getCount());

        WordCountResult fromDb = repository.findById(result.getId()).orElseThrow();

        assertEquals("test.txt", fromDb.getFileName());
        assertEquals(2L,
                fromDb.getWordCounts().stream()
                        .filter(w -> w.getWord().equals("java"))
                        .findFirst()
                        .orElseThrow()
                        .getCount());
    }

    @Test
    void getTopWords_shouldAggregateAcrossAllFiles() throws Exception {
        Path file1 = Files.createTempFile("f1", ".txt");
        Files.writeString(file1, "java java spring");
        wordCountService.processFile(file1, "file1.txt");

        Path file2 = Files.createTempFile("f2", ".txt");
        Files.writeString(file2, "java boot");
        wordCountService.processFile(file2, "file2.txt");

        Map<String, Long> top = wordCountService.getTopWords(3);

        assertEquals(3L, top.get("java"));
        assertEquals(1L, top.get("spring"));
        assertEquals(1L, top.get("boot"));
    }

    @Test
    void deleteResult_shouldReturnFalseForMissingId() {
        assertFalse(wordCountService.deleteResult(99999L));
    }

    @Test
    void deleteResult_shouldRemoveFromDatabase() throws Exception {
        Path file = Files.createTempFile("test", ".txt");
        Files.writeString(file, "hello world");
        WordCountResult saved = wordCountService.processFile(file, "del.txt");

        assertTrue(wordCountService.deleteResult(saved.getId()));
        assertFalse(repository.existsById(saved.getId()));
    }

    @Test
    void findTopWords_shouldAggregateCountsAcrossResults() {
        WordCountResult first = new WordCountResult("first.txt");
        first.addWordCount(new WordCount("java", 10));
        first.addWordCount(new WordCount("spring", 5));
        repository.save(first);

        WordCountResult second = new WordCountResult("second.txt");
        second.addWordCount(new WordCount("java", 7));
        second.addWordCount(new WordCount("docker", 3));
        repository.save(second);

        List<WordCountSummary> result = wordCountRepository.findTopWords();

        assertEquals(3, result.size());

        assertEquals("java", result.get(0).word());
        assertEquals(17L, result.get(0).count());

        assertEquals("spring", result.get(1).word());
        assertEquals(5L, result.get(1).count());

        assertEquals("docker", result.get(2).word());
        assertEquals(3L, result.get(2).count());
    }

    @Test
    void getStats_shouldReturnAggregatedStatistics() {
        WordCountResult first = new WordCountResult("first.txt");
        first.addWordCount(new WordCount("java", 10));
        first.addWordCount(new WordCount("spring", 5));
        repository.save(first);

        WordCountResult second = new WordCountResult("second.txt");
        second.addWordCount(new WordCount("java", 7));
        second.addWordCount(new WordCount("docker", 3));
        repository.save(second);

        StatsResponse stats = wordCountService.getStats();

        assertEquals(2L, stats.getTotalFilesProcessed());
        assertEquals(3, stats.getTotalUniqueWords());
        assertEquals("java", stats.getMostFrequentWord());
        assertEquals(17L, stats.getMostFrequentWordCount());
    }
}
