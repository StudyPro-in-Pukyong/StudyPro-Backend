package com.pknu.studypro.service;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.clazz.Pay;
import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.ClazzRepository;
import com.pknu.studypro.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClazzService {

    private final ClazzRepository clazzRepository;
    private final MemberRepository memberRepository;

    public Clazz createClazz(Clazz clazz, ClazzRequestDto.Ids ids) {
        // Member test 코드
//        Member teacher = new Member(Role.TEACHER, LoginType.KAKAO, "선생님", null, "닉네임1");
//        Member teacher2 = new Member(Role.TEACHER, LoginType.KAKAO, "선생님2", null, "닉네임2");
//        memberRepository.save(teacher);
//        memberRepository.save(teacher2);

        // ------------------------------------------------------
        // memberId 식별하는 코드 작성하기
        // member들이 one to one 매핑되어 있어서 같은 memberId 등록 X -> 수정 필요해보임
        clazz.setTeacher(
                memberRepository.findById(ids.getTeacherId()).orElseThrow(
                        () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)
                )
        );
        if (ids.getParentId() != null) clazz.setTeacher(memberRepository.findById(ids.getParentId()).get());
        if (ids.getStudentId() != null) clazz.setTeacher(memberRepository.findById(ids.getStudentId()).get());

        // request body example
        /*
                {
                "title" : "영어 과외",
                "subject" : "영어",
                "postPay" : {
                    "amount" : "20000",
                    "date" : null,
                    "round" : "10"
                },
                "ids" : {
                    "teacherId" : 1,
                    "parentId" : null,
                    "studentId" : null
                }
}
         */
        // ------------------------------------------------------

        return clazzRepository.save(clazz);
    }

    public void deleteClazz(long clazzId) {
        //class가 있는지 없는지 검증하기
        Clazz clazz = verifiedClazz(clazzId);
        clazzRepository.delete(clazz);
    }

    public Clazz verifiedClazz(long clazzId) {
        Optional<Clazz> clazz = clazzRepository.findById(clazzId);
        return clazz.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CLASS_NOT_FOUND));
    }

    public Clazz updateClazz(Clazz clazz, ClazzRequestDto.Ids ids, long clazzId) {
        clazz.setTeacher(memberRepository.findById(ids.getTeacherId()).get());
        if (ids.getParentId() != null) clazz.setTeacher(memberRepository.findById(ids.getParentId()).get());
        if (ids.getStudentId() != null) clazz.setTeacher(memberRepository.findById(ids.getStudentId()).get());
        clazz.setClassId(clazzId);
        return clazzRepository.save(clazz);
    }

    public List<Clazz> getClazz(long teacherId){
        List<Clazz> clazz = clazzRepository.findByTeacherId(teacherId);
        return clazz;
    }
}
