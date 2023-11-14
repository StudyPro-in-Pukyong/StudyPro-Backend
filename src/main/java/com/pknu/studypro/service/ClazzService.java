package com.pknu.studypro.service;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.clazz.Pay;
import com.pknu.studypro.domain.clazz.RoundPay;
import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.ClazzRepository;
import com.pknu.studypro.repository.MemberRepository;
import com.pknu.studypro.repository.PayRepository;
import jakarta.transaction.Transactional;
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
    private final PayRepository payRepository;

    public Clazz createClazz(Clazz clazz, ClazzRequestDto.Ids ids) {
//         Member test 코드
        Member teacher = new Member(Role.TEACHER, LoginType.KAKAO, "선생님", null, "선생님 닉네임1");
        Member teacher2 = new Member(Role.TEACHER, LoginType.KAKAO, "선생님2", null, "선생님 닉네임2");
        Member student = new Member(Role.STUDENT, LoginType.KAKAO, "학생1", null, "학생 닉네임1");
        Member parent = new Member(Role.PARENT, LoginType.KAKAO, "학부모1", null, "학부모 닉네임1");
        memberRepository.save(teacher);
        memberRepository.save(teacher2);
        memberRepository.save(student);
        memberRepository.save(parent);

        // ------------------------------------------------------
        // memberId 식별하는 코드 작성하기
        // member들이 one to one 매핑되어 있어서 같은 memberId 등록 X -> 수정 필요해보임
        // class 관련 member 연관관계 성립
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

    @Transactional
    public Clazz updateClazz(Clazz clazz, ClazzRequestDto.Ids ids) {
        Clazz beforeClazz = verifiedClazz(clazz.getId());

        // Pay 방법이 바뀌는지 확인하기
        String beforePay = beforeClazz.getPay().getClass().getName();
        String afterPay = clazz.getPay().getClass().getName();
        if(beforePay.equals(afterPay)) { // 이전과 같은 Pay인 경우
            clazz.getPay().setId(beforeClazz.getPay().getId());
        } else { // Pay 방법이 바뀌는 경우
            // CurrentRound = 0 이 아닌 경우에는 에러 발생시키기
            if(beforeClazz.getPay().getCurrentRound() > 0)
                throw new BusinessLogicException(ExceptionCode.NOT_CHANGE_PAY_TYPE);
        }

        // class 관련 member 연관관계 성립
        clazz.setTeacher(memberRepository.findById(ids.getTeacherId()).get());
        if (ids.getParentId() != null) clazz.setTeacher(memberRepository.findById(ids.getParentId()).get());
        if (ids.getStudentId() != null) clazz.setTeacher(memberRepository.findById(ids.getStudentId()).get());

        payRepository.save(clazz.getPay());
        return clazzRepository.save(clazz);
    }

    public List<Clazz> getClazz(long teacherId){
        List<Clazz> clazz = clazzRepository.findByTeacherId(teacherId);
        return clazz;
    }
}
