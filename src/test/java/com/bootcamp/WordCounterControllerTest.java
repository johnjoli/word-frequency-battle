package com.bootcamp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WordCounterController.class)
@Import(WordCounter.class)
public class WordCounterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadFile_shouldReturnWordCounts() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "test/plain",
                "Java, java! Spring?".getBytes()
        );

        mockMvc.perform(multipart("/api/words/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.java").value(2))
                .andExpect(jsonPath("$.spring").value(1));
    }

    @Test
    void uploadFile_emptyFile_shouldReturnEmptyJson() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                "".getBytes()
        );

        mockMvc.perform(multipart("/api/words/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
