package com.pknu.studypro.controller;

import com.pknu.studypro.controller.auth.Auth;
import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.dto.LessonRequestDto;
import com.pknu.studypro.dto.LessonResponseDto;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.mapper.LessonMapper;
import com.pknu.studypro.service.LessonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@Validated
@AllArgsConstructor
public class LessonController {
    private final LessonService lessonService;
    private final LessonMapper lessonMapper;

    // lesson 화면 전환
    @GetMapping("/lesson")
    public String clazz(Model model) {
        return "lesson"; // clazz.html로 이동
    }

    // lesson 생성 화면 전환
    @GetMapping("/createLesson")
    public String createLesson(Model model) {
        return "createLesson"; // createLesson.html로 이동
    }

    // lesson 수정 화면 전환
    @PutMapping("/updateLesson")
    public String updateLesson(@RequestBody LessonResponseDto.ModelResponse lesson, Model model) {
        model.addAttribute("id", lesson.getId());
        model.addAttribute("date", lesson.getDate());
        model.addAttribute("startTime", lesson.getStartTime());
        model.addAttribute("endTime", lesson.getEndTime());
        model.addAttribute("isDone", lesson.getIsDone());
        model.addAttribute("type", lesson.getType());
        model.addAttribute("progress", lesson.getProgress());
        model.addAttribute("homeworks", lesson.getHomeworks());
        return "updateLesson"; // updateLesson.html로 이동
    }

    // CREATE
    @PostMapping("/lesson")
    public ResponseEntity postLesson(@Auth LoginUser loginUser,
                                     @Valid @RequestBody LessonRequestDto.Post post) {
        Lesson lesson = lessonService.createLesson(lessonMapper.lessonPostDtoToLesson(post));
        LessonResponseDto.Response response = lessonMapper.lessonToLessonResponseDto(lesson);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity getLesson(@Auth LoginUser loginUser,
                                    @Positive @PathVariable("lessonId") long lessonId) {
        Lesson lesson = lessonService.readLesson(lessonId);
        LessonResponseDto.Response response = lessonMapper.lessonToLessonResponseDto(lesson);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 레슨 목록 조회(선생님)
    // 레슨 목록 조회(부모님)
    // 레슨 목록 조회(학생)
    @GetMapping("/lessons")
    public ResponseEntity getLessons(@Auth LoginUser loginUser,
                                     @RequestParam("year") int year,
                                     @RequestParam("month") int month,
                                     @RequestParam("classId") long classId) {
        System.out.println(year + " " + month);
        List<Lesson> lessons = lessonService.readLessons(LocalDate.of(year, month, 1), classId);
        List<LessonResponseDto.Response> responses = lessonMapper.lessonsToLessonResponseDtos(lessons);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // UPDATE
    @PutMapping("/lesson")
    public ResponseEntity putLesson(@Auth LoginUser loginUser,
                                    @Valid @RequestBody LessonRequestDto.Put put) {
        Lesson lesson = lessonService.updateLesson(lessonMapper.lessonPutDtoToLesson(put));
        LessonResponseDto.Response response = lessonMapper.lessonToLessonResponseDto(lesson);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/lesson/{lessonId}")
    public ResponseEntity putIsDoneLesson(@Auth LoginUser loginUser,
                                          @Positive @PathVariable("lessonId") long lessonId,
                                          @RequestParam("isDone") boolean isDone) {
        Lesson lesson = lessonService.updateIsDoneLesson(lessonId, isDone);
        LessonResponseDto.Response response = lessonMapper.lessonToLessonResponseDto(lesson);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // DELETE
    @DeleteMapping("/lesson/{lessonId}")
    public ResponseEntity deleteLesson(@Auth LoginUser loginUser,
                                       @Positive @PathVariable("lessonId") long lessonId) {
        lessonService.deleteLesson(lessonId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
