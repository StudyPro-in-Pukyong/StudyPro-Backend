package com.pknu.studypro.mapper;

import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.dto.LessonRequestDto;
import com.pknu.studypro.dto.LessonResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    Lesson lessonPostDtoToLesson(LessonRequestDto.Post post);
    Lesson lessonPutDtoToLesson(LessonRequestDto.Post put);
    LessonResponseDto.Response lessonToLessonResponseDto(Lesson lesson);
}