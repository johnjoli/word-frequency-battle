package com.bootcamp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class WordCounter {
    public Map<String, Long> countWords(Path filePath) throws IOException {

        String content = Files.readString(filePath).toLowerCase().replaceAll("[^a-zа-я0-9\\s]", " ");
        String[] words = content.split("\\s+");

        Map<String, Long> map = new HashMap<>();

        for (String word : words) {
            if (!word.isEmpty()) {
                map.merge(word, 1L, Long::sum);
            }
        }
        return map;
    }

    public static void main(String[] args) throws IOException {
        WordCounter wordCounter = new WordCounter();
        try {
            Map<String, Long> map = wordCounter.countWords(Path.of("file.txt"));

            map.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}
