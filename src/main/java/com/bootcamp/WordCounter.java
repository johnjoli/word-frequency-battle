package com.bootcamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class WordCounter {
    private static final Logger logger = LoggerFactory.getLogger(WordCounter.class);

    public Map<String, Long> countWords(Path filePath) throws IOException {
        String content = Files.readString(filePath)
                .toLowerCase().
                replaceAll("[^a-zа-я0-9\\s]", " ");

        String[] words = content.split("\\s+");

        Map<String, Long> map = new HashMap<>();

        for (String word : words) {
            if (!word.isEmpty()) {
                map.merge(word, 1L, Long::sum);
            }
        }
        logger.debug("Подсчитано {} уникальных слов из файла {}", map.size(), filePath);
        return map;
    }
}
