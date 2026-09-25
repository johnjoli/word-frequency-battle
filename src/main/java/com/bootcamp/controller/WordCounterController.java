package com.bootcamp.controller;

import com.bootcamp.dto.StatsResponse;
import com.bootcamp.dto.WordCountResultDto;
import com.bootcamp.dto.WordCountResultSummaryDto;
import com.bootcamp.entity.WordCountResult;
import com.bootcamp.exception.EmptyFileException;
import com.bootcamp.exception.FileProcessingException;
import com.bootcamp.service.WordCountService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/words")
@Validated
public class WordCounterController {

    private final WordCountService wordCountService;

    @Autowired
    public WordCounterController(WordCountService wordCountService) {
        this.wordCountService = wordCountService;
    }

    @PostMapping("/upload")
    public ResponseEntity<WordCountResult> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            throw new EmptyFileException("Файл не выбран");
        }

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("upload_", ".txt");
            file.transferTo(tempFile.toFile());
            WordCountResult result = wordCountService.processFile(tempFile, file.getOriginalFilename());
            return ResponseEntity.ok(result);
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
    public ResponseEntity<Page<WordCountResultSummaryDto>> getHistory(
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("processedAt").descending());
        return ResponseEntity.ok(wordCountService.getHistory(fileName, from, to, pageable));
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<WordCountResultDto> getResultById(@PathVariable Long id) {
        WordCountResultDto dto = wordCountService.getResultById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        if (!wordCountService.deleteResult(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top")
    public ResponseEntity<Map<String, Long>> getTopWords(
            @RequestParam(defaultValue = "${word-counter.default-top-limit}")
            @Min(value = 1, message = "limit must be at least 1")
            @Max(value = 100, message = "limit must be at most 100")
            int limit) {
        return ResponseEntity.ok(wordCountService.getTopWords(limit));
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(wordCountService.getStats());
    }
}