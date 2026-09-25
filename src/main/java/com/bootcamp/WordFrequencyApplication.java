package com.bootcamp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WordFrequencyApplication {
    public static void main(String[] args) {
        SpringApplication.run(WordFrequencyApplication.class, args);
    }
}
