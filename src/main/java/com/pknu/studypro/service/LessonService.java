package com.pknu.studypro.service;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.ClazzRepository;
import com.pknu.studypro.repository.LessonRepository;
import com.pknu.studypro.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ClazzRepository clazzRepository;
    private final MemberRepository memberRepository;

    // CREATE
    public Lesson createLesson(Lesson lesson) {
        // --------------------------------------------------------------------------
        // classId 증명하는 코드 추가하기
        clazzRepository.findById(lesson.getClassId());
        // --------------------------------------------------------------------------

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
        // --------------------------------------------------------------------------
        // classId 증명하는 코드 추가하기
        Clazz clazz = clazzRepository.findById(classId).get();
        // --------------------------------------------------------------------------

        // 시작일
        LocalDateTime start = localDate.withDayOfMonth(1).atTime(LocalTime.MIN);

        // 마지막일
        LocalDateTime finish = localDate.withDayOfMonth(localDate.lengthOfMonth()).atTime(LocalTime.MAX);

        return lessonRepository.findByStartTimeBetween(start, finish);
    }

    // UPDATE
    public Lesson updateLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    // isDone = false → true : Pay의 currentRound 증가
    // isDone = true → false : Pay의 currentRound 감소
    public Lesson updateIsDoneLesson(long lessonId, boolean isDone) {
        // 기초 엔티티 → 삭제 예정
//        Member teacher = new Member(Role.TEACHER, LoginType.KAKAO, "선생님", null, "닉네임1");
//        memberRepository.save(teacher);
//        FixedDatePay fixedDatePay = new FixedDatePay(20000, 10);
//        Clazz clazz = new Clazz(fixedDatePay, "수학 과외", teacher, teacher, teacher);
//        clazzRepository.save(clazz);
        // --------------------------------------------------------------------------------------------

        Lesson lesson = verifiedLesson(lessonId);
        if(lesson.getIsDone() != isDone) { // isDone의 값 변경발생
            lesson.setIsDone(isDone);

            // --------------------------------------------------------------------------

            // classId 증명하는 코드 추가하기
            Clazz clazz = clazzRepository.findById(lesson.getClassId()).get();

            // --------------------------------------------------------------------------
            if(isDone) { // isDone = false → true : Pay의 currentRound 증가
                clazz.getPay().setPlusCurrentRound();
            } else { // isDone = true → false : Pay의 currentRound 감소
                clazz.getPay().setMinusCurrentRound();
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
