package com.pknu.studypro.controller;

import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.dto.LessonRequestDto;
import com.pknu.studypro.dto.LessonResponseDto;
import com.pknu.studypro.mapper.LessonMapper;
import com.pknu.studypro.service.LessonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class LessonController {
    private final LessonService lessonService;
    private final LessonMapper lessonMapper;

    // CREATE
    @PostMapping("/lesson")
    public ResponseEntity postLesson(@Valid @RequestBody LessonRequestDto.Post post) {
        Lesson lesson = lessonService.createLesson(lessonMapper.lessonPostDtoToLesson(post));
        LessonResponseDto.Response response = lessonMapper.lessonToLessonResponseDto(lesson);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity getLesson(@Positive @PathVariable("lessonId") long lessonId) {
        Lesson lesson = lessonService.readLesson(lessonId);
        LessonResponseDto.Response response = lessonMapper.lessonToLessonResponseDto(lesson);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 레슨 목록 조회(선생님)
    // 레슨 목록 조회(부모님)
    // 레슨 목록 조회(학생)
    @GetMapping("/lessons")
    public ResponseEntity getLessons(@RequestParam("year") int year,
                                     @RequestParam("month") int month,
                                     @RequestParam("classId") long classId) {
        List<Lesson> lessons = lessonService.readLessons(LocalDate.of(year, month, 1), classId);
        List<LessonResponseDto.Response> responses = lessonMapper.lessonsToLessonResponseDtos(lessons);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // UPDATE
    @PutMapping("/lesson")
    public ResponseEntity putLesson(@Valid @RequestBody LessonRequestDto.Put put) {
        Lesson lesson = lessonService.updateLesson(lessonMapper.lessonPutDtoToLesson(put));
        LessonResponseDto.Response response = lessonMapper.lessonToLessonResponseDto(lesson);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/lesson/{lessonId}")
    public ResponseEntity putIsDoneLesson(@Positive @PathVariable("lessonId") long lessonId,
                                          @RequestParam("isDone") boolean isDone) {
        Lesson lesson = lessonService.updateIsDoneLesson(lessonId, isDone);
        LessonResponseDto.Response response = lessonMapper.lessonToLessonResponseDto(lesson);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    // DELETE
    @DeleteMapping("/lesson/{lessonId}")
    public ResponseEntity deleteLesson(@Positive @PathVariable("lessonId") long lessonId) {
        lessonService.deleteLesson(lessonId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
