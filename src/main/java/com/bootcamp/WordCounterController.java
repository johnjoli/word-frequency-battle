package com.bootcamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/words")
public class WordCounterController {

    private final WordCounter wordCounter;

    @Autowired
    public WordCounterController(WordCounter wordCounter) {
        this.wordCounter = wordCounter;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Long>> uploadFile(@RequestParam("file") MultipartFile file) {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("upload_", ".txt");
            file.transferTo(tempFile.toFile());
            Map<String, Long> wordCounts = wordCounter.countWords(tempFile);
            return ResponseEntity.ok(wordCounts);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {}
            }
        }
    }
}