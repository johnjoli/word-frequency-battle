package com.bootcamp.mapper;

import com.bootcamp.dto.WordCountResultDto;
import com.bootcamp.dto.WordCountResultSummaryDto;
import com.bootcamp.entity.WordCount;
import com.bootcamp.entity.WordCountResult;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface WordCountResultMapper {

    WordCountResultDto toDto(WordCountResult entity);

    @Mapping(target = "uniqueWordsCount", ignore = true)
    WordCountResultSummaryDto toSummaryDto(WordCountResult entity);

    default Map<String, Long> mapWordCounts(List<WordCount> wordCounts) {
        return wordCounts.stream()
                .collect(Collectors.toMap(WordCount::getWord, WordCount::getCount));
    }

    @AfterMapping
    default void fillUniqueWordsCount(
            WordCountResult entity,
            @MappingTarget WordCountResultSummaryDto dto) {

        if (entity.getWordCounts() != null) {
            dto.setUniqueWordsCount(entity.getWordCounts().size());
        }
    }
}
