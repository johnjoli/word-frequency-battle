package com.bootcamp.health;

import com.bootcamp.repository.WordCountResultRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class WordCounterHealthIndicator implements HealthIndicator {

    private final WordCountResultRepository repository;

    public WordCounterHealthIndicator(WordCountResultRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        try {
            long count= repository.count();
            return Health.up()
                    .withDetail("totalResults", count)
                    .withDetail("service", "word-counter")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
