package com.pknu.studypro.service;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.ClazzDate;
import com.pknu.studypro.domain.clazz.ClazzTime;
import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.domain.lesson.Type;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.ClazzRepository;
import com.pknu.studypro.repository.LessonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

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

    public List<Lesson> createLessons(long classId, int year, int month) {
        Clazz clazz = clazzService.verifiedClazz(classId);
        List<Lesson> lessons = new ArrayList<>();

        // 각 요일의 목록
        Map<ClazzDate, DayOfWeek> clazzDateToDayOfWeek = new HashMap<>();
        int idx = 1;
        for (ClazzDate clazzDate : ClazzDate.values()) {
            clazzDateToDayOfWeek.put(clazzDate, DayOfWeek.of(idx++));
        }

        // classTime의 요일 저장
        List<ClazzTime> clazzTimes = clazz.getClazzTimes();
        Map<DayOfWeek, List<ClazzTime>> clazzTimesMap = new HashMap<>();
        for (ClazzTime clazzTime : clazzTimes) {
            DayOfWeek dayOfWeek = clazzDateToDayOfWeek.get(clazzTime.getClazzDate());
            List<ClazzTime> clazzTimesList = clazzTimesMap.getOrDefault(dayOfWeek, new ArrayList<>());
            clazzTimesList.add(clazzTime);
            clazzTimesMap.put(dayOfWeek, clazzTimesList);
        }
        System.out.println(clazzTimesMap);

        // 해당 월의 첫 날 설정
        LocalDate date = LocalDate.of(year, month, 1);
        // 해당 월의 마지막 날 구하기
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        // 첫 날부터 마지막 날까지 반복
        while (!date.isAfter(endOfMonth)) {
            // 현재 날짜의 요일이 지정된 요일 리스트에 포함되어 있는 경우
            if (clazzTimesMap.containsKey(date.getDayOfWeek())) {
                for (ClazzTime clazzTime : clazzTimesMap.get(date.getDayOfWeek())) {
                    // 이미 같은 class에 같은 날짜&시간이 아닌 경우에만 새로운 lesson 생성
                    if(!lessonRepository.existsByClassIdAndStartTime(clazz.getId(), date.atTime(clazzTime.getStartTime()))){
                        lessons.add(new Lesson(
                                null,
                                clazz.getId(),
                                date.atTime(clazzTime.getStartTime()),
                                (int) Duration.between(clazzTime.getStartTime(), clazzTime.getEndTime()).toMinutes(),
                                Type.ORIGINAL,
                                false,
                                null
                        ));
                    }
                }
            }
            // 다음 날로 이동
            date = date.plusDays(1);
        }

        return lessonRepository.saveAll(lessons);
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

        return lessonRepository.findByStartTimeBetweenAndClassId(start, finish, classId);
    }

    // UPDATE
    public Lesson updateLesson(Lesson lesson) {
        updateIsDoneLesson(lesson.getId(), lesson.getIsDone());
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
        if(lesson.getIsDone()) {
            Clazz clazz = clazzService.verifiedClazz(lesson.getClassId());
            clazz.getPay().setMinusCurrentRoundAndTotalTime(lesson.getMinutes());
        }
        lessonRepository.delete(lesson);
    }

    // 증명
    public Lesson verifiedLesson(long lessonId) {
        Optional<Lesson> lesson = lessonRepository.findById(lessonId);
        return lesson.orElseThrow(() -> new BusinessLogicException(ExceptionCode.LESSON_NOT_FOUND));
    }
}
