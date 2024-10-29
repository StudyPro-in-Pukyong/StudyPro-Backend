package com.pknu.studypro.service;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.ClazzRepository;
import com.pknu.studypro.repository.LessonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ClazzService clazzService;
    private final ClazzRepository clazzRepository;

    // CREATE
    public Lesson createLesson(Lesson lesson) {
        clazzService.verifiedClazz(lesson.getClassId());
        return lessonRepository.save(lesson);
    }

    // READ
    public Lesson readLesson(long lessonId) {
        return verifiedLesson(lessonId);
    }

    // 레슨 목록 조회(선생님)
    // 레슨 목록 조회(부모님)
    // 레슨 목록 조회(학생)
    public List<Lesson> readLessons(LocalDate localDate, long classId) {
        // classId 증명
        Clazz clazz = clazzService.verifiedClazz(classId);

        // 시작일 -> 요청한 달 - 1
        LocalDateTime start = localDate.atTime(LocalTime.MIN);

        // 마지막일 -> 요청한 달 + 1
        LocalDateTime finish = localDate.withDayOfMonth(localDate.lengthOfMonth()).atTime(LocalTime.MAX);

        System.out.println("!! " + start + " " + finish);
        System.out.println(localDate);

        return lessonRepository.findByStartTimeBetweenAndClassId(start, finish, classId);
    }

    // UPDATE
    public Lesson updateLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    // isDone = false → true : Pay의 currentRound 증가
    // isDone = true → false : Pay의 currentRound 감소
    public Lesson updateIsDoneLesson(long lessonId, boolean isDone) {
        Lesson lesson = verifiedLesson(lessonId);
        if(lesson.getIsDone() != isDone) { // isDone의 값 변경발생
            lesson.setIsDone(isDone);

            Clazz clazz = clazzService.verifiedClazz(lesson.getClassId());
            if(isDone) { // isDone = false → true : Pay의 currentRound 증가 & totalTime 증가
                clazz.getPay().setPlusCurrentRoundAndTotalTime(lesson.getMinutes());
            } else { // isDone = true → false : Pay의 currentRound 감소 & totalTime 감소
                clazz.getPay().setMinusCurrentRoundAndTotalTime(lesson.getMinutes());
            }
        }

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
