package com.bootcamp.service;

import com.bootcamp.entity.WordCountResult;
import com.bootcamp.mapper.WordCountResultMapperImpl;
import com.bootcamp.repository.WordCountResultRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


@DataJpaTest
@Import({WordCountService.class, WordCounter.class, WordCountResultMapperImpl.class})
@ActiveProfiles("test")
class WordCountServiceTest {

    @Autowired
    private WordCountService wordCountService;

    @Autowired
    private WordCountResultRepository repository;

    @Test
    void processFile_shouldSaveResultToDataBase() throws Exception {
        Path file = Files.createTempFile("test", ".txt");
        Files.writeString(file, "Java, java! Spring?");

        WordCountResult result = wordCountService.processFile(file, "test.txt");

        assertNotNull(result.getId(), "id должен быть проставлен после сохранения");
        assertEquals("test.txt", result.getFileName());
        assertEquals(2L, result.getWordCounts().get("java"));
        assertEquals(1L, result.getWordCounts().get("spring"));

        WordCountResult fromDb = repository.findById(result.getId()).orElseThrow();
        assertEquals("test.txt", fromDb.getFileName());
        assertEquals(2L, fromDb.getWordCounts().get("java"));
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

}
