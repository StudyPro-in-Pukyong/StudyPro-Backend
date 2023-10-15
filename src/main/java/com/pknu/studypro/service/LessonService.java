package com.pknu.studypro.service;

import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.LessonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;

    // CREATE
    public Lesson createLesson(Lesson lesson) {
        // classId 증명하는 코드 추가하기
        return lessonRepository.save(lesson);
    }

    // READ
    public Lesson readLesson(long lessonId) {
        return verifiedLesson(lessonId);
    }

    // UPDATE
    public Lesson updateLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    // DELETE
    public void deleteLesson(long lessonId) {
        Lesson lesson = verifiedLesson(lessonId);
        lessonRepository.delete(lesson);
    }

    // 증명
    public Lesson verifiedLesson(long lessonId) {
        Optional<Lesson> lesson = lessonRepository.findById(lessonId);
        return lesson.orElseThrow(() -> new BusinessLogicException(ExceptionCode.LESSON_NOT_FOUND));
    }
}
