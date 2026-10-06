package com.bootcamp.service;

import com.bootcamp.config.WordCounterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class WordCounter {
    private static final Logger logger = LoggerFactory.getLogger(WordCounter.class);

    private final WordCounterProperties properties;

    public WordCounter(WordCounterProperties properties) {
        this.properties = properties;
    }

    public Map<String, Long> countWords(Path filePath) throws IOException {
        try (BufferedReader reader =
                     Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {

            String line;
            Map<String, Long> map = new HashMap<>();
            int minLen = properties.getMinWordLength();
            boolean countNumbers = properties.getProcessing().isCountNumbers();

            while ((line = reader.readLine()) != null) {

                String normalized = line
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("[^a-zа-яё0-9\\s]", " ");

                String[] words = normalized.split("\\s+");

                for (String word : words) {
                    if (word.isEmpty()) continue;
                    if (word.length() < minLen) continue;
                    if (!countNumbers && word.matches("\\d+")) continue;
                    map.merge(word, 1L, Long::sum);
                }
            }
            logger.debug("Подсчитано {} уникальных слов из файла {} (minLen={}, countNumbers={})",
                    map.size(), filePath, minLen, countNumbers);
            return map;
        }
    }
}