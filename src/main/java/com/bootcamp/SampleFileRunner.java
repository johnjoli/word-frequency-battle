package com.bootcamp;

import com.bootcamp.service.WordCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Component
public class SampleFileRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(SampleFileRunner.class);
    private final WordCounter wordCounter;

    public SampleFileRunner(WordCounter wordCounter) {
        this.wordCounter = wordCounter;
    }

    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = new ClassPathResource("sample.txt");
        Path tempFile = Files.createTempFile("sample_", ".txt");
        try (InputStream in = resource.getInputStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        Map<String, Long> counts = wordCounter.countWords(tempFile);

        if (counts.isEmpty()) {
            logger.info("No words found in sample.txt");
        } else {
            counts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> logger.info("{} : {}", entry.getKey(), entry.getValue()));
        }

        Files.deleteIfExists(tempFile);
    }
}