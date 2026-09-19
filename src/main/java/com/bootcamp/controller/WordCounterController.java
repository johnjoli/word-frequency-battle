package com.bootcamp.controller;

import com.bootcamp.entity.WordCountResult;
import com.bootcamp.exception.EmptyFileException;
import com.bootcamp.exception.FileProcessingException;
import com.bootcamp.repository.WordCountResultRepository;
import com.bootcamp.service.WordCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;


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
    public ResponseEntity<Page<WordCountResult>> getHistory(

            @RequestParam(required = false) String fileName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("processedAt").descending());
        Page<WordCountResult> history = repository.findWithFilters(fileName, from, to, pageable);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<WordCountResult> getResultById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}