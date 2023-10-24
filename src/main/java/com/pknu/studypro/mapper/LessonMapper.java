package com.pknu.studypro.mapper;

import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.dto.LessonRequestDto;
import com.pknu.studypro.dto.LessonResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    Lesson lessonPostDtoToLesson(LessonRequestDto.Post post);
    Lesson lessonPutDtoToLesson(LessonRequestDto.Put put);
    LessonResponseDto.Response lessonToLessonResponseDto(Lesson lesson);
    List<LessonResponseDto.Response> lessonsToLessonResponseDtos(List<Lesson> lesson);
}