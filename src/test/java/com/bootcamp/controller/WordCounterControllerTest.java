package com.bootcamp.controller;

import com.bootcamp.entity.WordCountResult;
import com.bootcamp.service.WordCountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordCounterController.class)
public class WordCounterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordCountService wordCountService;

    @Test
    void uploadFile_shouldReturnWordCounts() throws Exception {
        WordCountResult mocked = new WordCountResult("test.txt", Map.of("java", 2L, "spring", 1L));
        when(wordCountService.processFile(any(), eq("test.txt"))).thenReturn(mocked);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain",
                "Java, java! Spring?".getBytes()
        );

        mockMvc.perform(multipart("/api/words/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.wordCounts.java").value(2))
                .andExpect(jsonPath("$.wordCounts.spring").value(1));
    }

    @Test
    void uploadFile_noFile_shouldReturn400() throws Exception {
        mockMvc.perform(multipart("/api/words/upload"))
                .andExpect(status().isBadRequest());
    }
}