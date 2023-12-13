package com.pknu.studypro.service;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.ClazzTime;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.clazz.RoundPay;
import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.ClazzRepository;
import com.pknu.studypro.repository.ClazzTimeRepository;
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
    private final ClazzTimeRepository clazzTimeRepository;

    public Clazz createClazz(Clazz clazz, ClazzRequestDto.Ids ids) {
//         Member test 코드
//        Member teacher = new Member(Role.TEACHER, LoginType.KAKAO, "선생님", null, "선생님 닉네임1");
//        Member teacher2 = new Member(Role.TEACHER, LoginType.KAKAO, "선생님2", null, "선생님 닉네임2");
//        Member student = new Member(Role.STUDENT, LoginType.KAKAO, "학생1", null, "학생 닉네임1");
//        Member parent = new Member(Role.PARENT, LoginType.KAKAO, "학부모1", null, "학부모 닉네임1");
//        memberRepository.save(teacher);
//        memberRepository.save(teacher2);
//        memberRepository.save(student);
//        memberRepository.save(parent);

        // 수업일정 설정
        for(ClazzTime clazzTime : clazz.getClazzTimes()) {
            clazzTime.setClazz(clazz);
        }

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
                "title": "영어 과외",
                "subject": "영어",
                "postPay": {
                    "amount": "20000",
                    "date": "2023-11-27",
                    "round": null
                },
                "ids": {
                    "teacherId": 1,
                    "parentId": null,
                    "studentId": null
                },
                "clazzTimes": [
                    {
                        "clazzDate": "MON",
                        "startTime": "09:00:00",
                        "endTime": "10:00:00"
                    },
                    {
                        "clazzDate": "TUE",
                        "startTime": "09:00:00",
                        "endTime": "10:00:00"
                    }
                ]
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
            if(!checkCurrentRound(clazz))
                throw new BusinessLogicException(ExceptionCode.NOT_CHANGE_PAY_TYPE);
        }

        // 수업일정이 바뀌었는지 확인하기
//        for(ClazzTime clazzTime : beforeClazz.getClazzTimes()) {
//            clazzTime.equals()
//        }
//        clazzTimeRepository.delete();
        for(ClazzTime clazzTime : clazz.getClazzTimes()) {
            clazzTime.setClazz(clazz);
        }


        // class 관련 member 연관관계 성립
        clazz.setTeacher(memberRepository.findById(ids.getTeacherId()).get());
        if (ids.getParentId() != null) clazz.setTeacher(memberRepository.findById(ids.getParentId()).get());
        if (ids.getStudentId() != null) clazz.setTeacher(memberRepository.findById(ids.getStudentId()).get());

        payRepository.save(clazz.getPay());
        return clazzRepository.save(clazz);
    }

    // 수업일정이 바뀌었는지 확인하기
    public void checkClazzTime(Clazz clazz, Clazz beforeClazz) {
//        for()
    }

    public List<Clazz> getClazz(long teacherId){
        List<Clazz> clazz = clazzRepository.findByTeacherId(teacherId);
        return clazz;
    }

    // 정산 요청하기
    public Clazz settleRequestClazz(long clazzId) {
        Clazz clazz = verifiedClazz(clazzId);

        // Pay 방법에 따른 정산방법 분류
        if(clazz.getPay().getClass().getName().contains("FixedDatePay")) { // FixedDatePay
            FixedDatePay pay = (FixedDatePay) clazz.getPay();
            // TODO : 알람기능 만들기

            // 이전 date가 localDate 타입이었을 경우 코드
//            LocalDate date = pay.getDate();

            // 오늘 날짜가 pay의 date보다 크거나 같은 경우 정산 허용(다음달부터)
//            if(date.isBefore(date.plusMonths(1)) && date.getDayOfMonth() <= LocalDate.now().getDayOfMonth()) {
//                // TODO : 알람기능 만들기
//            } else { // 정산 허용 X
//                throw new BusinessLogicException(ExceptionCode.NOT_ALLOW_SETTLE);
//            }
        } else if(clazz.getPay().getClass().getName().contains("RoundPay")) { // RoundPay
            RoundPay pay = (RoundPay) clazz.getPay();
            int round = pay.getRound();

            // 현재 round가 지정한 round보다 클 경우 정산 허용
            if(clazz.getPay().getCurrentRound() >= round) {
                // TODO : 알람기능 만들기
            } else { // 정산 허용 X
                throw new BusinessLogicException(ExceptionCode.NOT_ALLOW_SETTLE);
            }
        }

        return clazz;
    }

    // 정산 허용하기
    public Clazz settleAllowClazz(long clazzId) {
        Clazz clazz = verifiedClazz(clazzId);
        clazz.getPay().settle();
        return clazz;
    }

    // 정산방법 바꾸기 전 정산 완료여부 확인
    public boolean checkCurrentRound(Clazz clazz) {
        if(clazz.getPay().getCurrentRound() == 0) return true; // 정산방법 바꾸기 OK
        return false; // 정산방법 바꾸기 X
    }
}
