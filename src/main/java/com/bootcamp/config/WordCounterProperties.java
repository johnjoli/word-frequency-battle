package com.bootcamp.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "word-counter")
@Validated
public class WordCounterProperties {

    @Min(1)
    @Max(100)
    private int defaultTopLimit = 10;

    @Min(1)
    @Max(1000)
    private int maxTopLimit = 100;

    @Min(1)
    private int minWordLength = 1;

    private Processing processing = new Processing();

    public static class Processing {

        private boolean countNumbers = true;

        @NotBlank
        private String allowedExtension = ".txt";

        public boolean isCountNumbers() { return countNumbers; }
        public void setCountNumbers(boolean countNumbers) { this.countNumbers = countNumbers; }

        public String getAllowedExtension() { return allowedExtension; }
        public void setAllowedExtension(String allowedExtension) { this.allowedExtension = allowedExtension; }
    }

    public int getDefaultTopLimit() { return defaultTopLimit; }
    public void setDefaultTopLimit(int defaultTopLimit) { this.defaultTopLimit = defaultTopLimit; }

    public int getMaxTopLimit() { return maxTopLimit; }
    public void setMaxTopLimit(int maxTopLimit) { this.maxTopLimit = maxTopLimit; }

    public int getMinWordLength() { return minWordLength; }
    public void setMinWordLength(int minWordLength) { this.minWordLength = minWordLength; }

    public Processing getProcessing() { return processing; }
    public void setProcessing(Processing processing) { this.processing = processing; }
}