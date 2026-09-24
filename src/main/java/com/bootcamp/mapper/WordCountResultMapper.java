package com.bootcamp.mapper;

import com.bootcamp.dto.WordCountResultDto;
import com.bootcamp.dto.WordCountResultSummaryDto;
import com.bootcamp.entity.WordCountResult;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WordCountResultMapper {

    WordCountResultDto toDto(WordCountResult entity);

    @Mapping(target = "uniqueWordsCount", ignore = true)
    WordCountResultSummaryDto toSummaryDto(WordCountResult entity);

    @AfterMapping
    default void fillUniqueWordsCount(WordCountResult entity,
                                      @MappingTarget WordCountResultSummaryDto dto) {
        if (entity.getWordCounts() != null) {
            dto.setUniqueWordsCount(entity.getWordCounts().size());
        }
    }
}
