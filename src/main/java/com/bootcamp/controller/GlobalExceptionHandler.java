package com.bootcamp.controller;

import com.bootcamp.exception.EmptyFileException;
import com.bootcamp.exception.FileProcessingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmptyFileException.class)
    public ProblemDetail handleEmptyFile(EmptyFileException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problem.setTitle("Empty file");
        problem.setType(URI.create("https://api.wordcounter/errors/empty-file"));
        return problem;
    }

    @ExceptionHandler(FileProcessingException.class)
    public ProblemDetail handleProcessing(FileProcessingException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problem.setTitle("File processing error");
        problem.setType(URI.create("https://api.wordcounter/errors/file-processing"));
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException e) {
        String violations = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, violations);
        problem.setTitle("Validation failed");
        problem.setType(URI.create("https://api.wordcounter/errors/validation"));
        return problem;
    }
}