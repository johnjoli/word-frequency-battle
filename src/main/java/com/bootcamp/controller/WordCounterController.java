package com.bootcamp.controller;

import com.bootcamp.entity.WordCountResult;
import com.bootcamp.exception.EmptyFileException;
import com.bootcamp.exception.FileProcessingException;
import com.bootcamp.repository.WordCountResultRepository;
import com.bootcamp.service.WordCountService;
import com.bootcamp.service.WordCounter;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/words")
public class WordCounterController {

    private final WordCountService wordCountService;
    private final WordCountResultRepository repository;

    @Autowired
    public WordCounterController(WordCountService wordCountService,
                                 WordCountResultRepository repository) {
        this.wordCountService = wordCountService;
        this.repository = repository;
    }

    @PostMapping("/upload")
    public ResponseEntity<WordCountResult> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            throw new EmptyFileException("Файл не выбран");
        }

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("upload_", "txt");
            file.transferTo(tempFile.toFile());

            WordCountResult resul = wordCountService.processFile(tempFile, file.getOriginalFilename());

            return ResponseEntity.ok(resul);
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка обработки файла");
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {}
            }
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<WordCountResult>> getHistory() {
        List<WordCountResult> history = repository.findAll();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<WordCountResult> getResultById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}