package com.bootcamp;

import java.nio.file.Files;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message) {
        super(message);
    }
}
