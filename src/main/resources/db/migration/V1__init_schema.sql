CREATE TABLE word_count_results (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE word_counts (
    result_id BIGINT NOT NULL,
    word VARCHAR(255) NOT NULL,
    count BIGINT,
    PRIMARY KEY (result_id, word),
    CONSTRAINT fk_word_counts_result
        FOREIGN KEY (result_id)
        REFERENCES word_count_results (id)
);