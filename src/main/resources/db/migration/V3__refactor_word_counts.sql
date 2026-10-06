ALTER TABLE word_counts
    DROP CONSTRAINT word_counts_pkey;

ALTER TABLE word_counts
    ADD COLUMN id BIGSERIAL;

ALTER TABLE word_counts
    ADD CONSTRAINT word_counts_pkey PRIMARY KEY (id);