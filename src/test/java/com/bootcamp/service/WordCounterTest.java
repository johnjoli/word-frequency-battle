package com.bootcamp.service;

import com.bootcamp.config.WordCounterProperties;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WordCounterTest {

    private final WordCounter wordCounter = new WordCounter(new WordCounterProperties());

    @Test
    void testBasicCount() throws Exception {
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.writeString(tempFile, "Java, java! Java?");
        Map<String, Long> result = wordCounter.countWords(tempFile);
        assertEquals(3L, result.get("java"));
    }

    @Test
    void emptyFile_shouldReturnEmptyMap() throws Exception {
        Path file = createTempFile("");
        Map<String, Long> result = wordCounter.countWords(file);
        assertTrue(result.isEmpty());
    }

    @Test
    void onlyPunctuationAndWhitespace_shouldReturnEmptyMap() throws Exception {
        Path file = createTempFile(" ! @ # $ % ^ & * ( ) \t \n ");
        Map<String, Long> result = wordCounter.countWords(file);
        assertTrue(result.isEmpty());
    }

    @Test
    void numbersShouldBeAsWords() throws Exception {
        // countNumbers=true в дефолтной конфигурации
        Path file = createTempFile("123 456 123");
        Map<String, Long> result = wordCounter.countWords(file);
        assertEquals(1L, result.get("456"));
        assertEquals(2L, result.get("123"));
    }

    @Test
    void nonExistentFileShouldThrowsIOException() {
        Path file = Path.of("there_is_no_such_file.txt");
        assertThrows(IOException.class, () -> wordCounter.countWords(file));
    }

    @Test
    void countWords_withMinWordLength_shouldSkipShortWords() throws Exception {
        WordCounterProperties props = new WordCounterProperties();
        props.setMinWordLength(3);
        WordCounter counter = new WordCounter(props);

        Path file = createTempFile("ab abc abcd abc");
        Map<String, Long> result = counter.countWords(file);

        assertFalse(result.containsKey("ab"));
        assertEquals(2L, result.get("abc"));
        assertEquals(1L, result.get("abcd"));
    }

    private Path createTempFile(String content) throws Exception {
        Path file = Files.createTempFile("test", ".txt");
        Files.writeString(file, content);
        return file;
    }
}