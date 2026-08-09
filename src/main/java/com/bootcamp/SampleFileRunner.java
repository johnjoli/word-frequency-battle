package com.bootcamp;

import com.bootcamp.service.WordCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
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
        Path path = Path.of(resource.getURI());
        Map<String, Long> counts = wordCounter.countWords(path);

        if (counts.isEmpty()) {
            logger.info("No words found in sample.txt");
        } else {
            counts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> logger.info("{} : {}", entry.getKey(), entry.getValue()));
        }
    }
}