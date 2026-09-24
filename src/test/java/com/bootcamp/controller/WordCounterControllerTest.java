package com.bootcamp.controller;

import com.bootcamp.dto.StatsResponse;
import com.bootcamp.dto.WordCountResultDto;
import com.bootcamp.dto.WordCountResultSummaryDto;
import com.bootcamp.entity.WordCountResult;
import com.bootcamp.service.WordCountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordCounterController.class)
public class WordCounterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordCountService wordCountService;

    // upload

    @Test
    void uploadFile_shouldReturnWordCounts() throws Exception {
        WordCountResult mocked = new WordCountResult("test.txt", Map.of("java", 2L, "spring", 1L));
        when(wordCountService.processFile(any(), eq("test.txt"))).thenReturn(mocked);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain",
                "Java, java! Spring?".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/words/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.wordCounts.java").value(2))
                .andExpect(jsonPath("$.wordCounts.spring").value(1));
    }

    @Test
    void uploadFile_noFile_shouldReturn400() throws Exception {
        mockMvc.perform(multipart("/api/v1/words/upload"))
                .andExpect(status().isBadRequest());
    }

    // history

    @Test
    void getHistory_shouldReturnPageOfResults() throws Exception {
        WordCountResultSummaryDto dto1 = new WordCountResultSummaryDto();
        dto1.setId(1L);
        dto1.setFileName("a.txt");
        dto1.setUniqueWordsCount(3);

        WordCountResultSummaryDto dto2 = new WordCountResultSummaryDto();
        dto2.setId(2L);
        dto2.setFileName("b.txt");
        dto2.setUniqueWordsCount(2);

        Page<WordCountResultSummaryDto> page = new PageImpl<>(List.of(dto1, dto2), PageRequest.of(0, 10), 2);
        when(wordCountService.getHistory(any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/words/history").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].fileName").value("a.txt"))
                .andExpect(jsonPath("$.content[0].uniqueWordsCount").value(3))
                .andExpect(jsonPath("$.content[1].fileName").value("b.txt"))
                .andExpect(jsonPath("$.content[1].uniqueWordsCount").value(2));
    }

    // history/{id}

    @Test
    void getResultById_whenExists_shouldReturn200() throws Exception {
        WordCountResultDto dto = new WordCountResultDto();
        dto.setId(42L);
        dto.setFileName("test.txt");
        dto.setWordCounts(Map.of("java", 5L));
        when(wordCountService.getResultById(42L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/words/history/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.fileName").value("test.txt"))
                .andExpect(jsonPath("$.wordCounts.java").value(5));
    }

    @Test
    void getResultById_whenMissing_shouldReturn404() throws Exception {
        when(wordCountService.getResultById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/words/history/999"))
                .andExpect(status().isNotFound());
    }

    // delete

    @Test
    void deleteResult_whenExists_shouldReturn204() throws Exception {
        when(wordCountService.deleteResult(42L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/words/history/42"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteResult_whenMissing_shouldReturn404() throws Exception {
        when(wordCountService.deleteResult(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/words/history/999"))
                .andExpect(status().isNotFound());
    }

    // top

    @Test
    void getTopWords_shouldReturnMap() throws Exception {
        Map<String, Long> top = Map.of("java", 10L, "spring", 7L, "boot", 4L);
        when(wordCountService.getTopWords(3)).thenReturn(top);

        mockMvc.perform(get("/api/v1/words/top").param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.java").value(10))
                .andExpect(jsonPath("$.spring").value(7))
                .andExpect(jsonPath("$.boot").value(4));
    }

    @Test
    void getTopWords_invalidLimit_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/words/top").param("limit", "0"))
                .andExpect(status().isBadRequest());
    }

    // stats

    @Test
    void getStats_shouldReturnStatsResponse() throws Exception {
        StatsResponse stats = new StatsResponse(5L, 42, "java", 27L);
        when(wordCountService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/words/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFilesProcessed").value(5))
                .andExpect(jsonPath("$.totalUniqueWords").value(42))
                .andExpect(jsonPath("$.mostFrequentWord").value("java"))
                .andExpect(jsonPath("$.mostFrequentWordCount").value(27));
    }
}