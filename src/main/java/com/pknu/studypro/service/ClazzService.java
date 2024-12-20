package com.pknu.studypro.service;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.ClazzTime;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.clazz.RoundPay;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.ClazzRepository;
import com.pknu.studypro.repository.ClazzTimeRepository;
import com.pknu.studypro.repository.MemberRepository;
import com.pknu.studypro.repository.PayRepository;
import com.pknu.studypro.util.FindMember;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClazzService {
    private final ClazzRepository clazzRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PayRepository payRepository;
    private final ClazzTimeRepository clazzTimeRepository;
    private final FindMember findMember;

    @Transactional
    public Clazz createClazz(Clazz clazz, ClazzRequestDto.Ids ids, LoginUser loginUser) {
        // 수업일정 설정
        for (ClazzTime clazzTime : clazz.getClazzTimes()) {
            clazzTime.setClazz(clazz);
        }

        Member member = findMember.findMemberByToken(loginUser);
        if (member.getRole() == Role.TEACHER) {
            clazz.setTeacher(member);
        } else if (member.getRole() == Role.PARENT) {
            clazz.setParent(member);
        } else if (member.getRole() == Role.STUDENT) {
            clazz.setStudent(member);
        }

        // ------------------------------------------------------
        // memberId 식별하는 코드 작성하기
        // member들이 one to one 매핑되어 있어서 같은 memberId 등록 X -> 수정 필요해보임 -> 수정완료함
        // class 관련 member 연관관계 성립
        classSetMembers(ids, clazz);

        return clazzRepository.save(clazz);
    }

    public void deleteClazz(long clazzId, LoginUser loginUser) {
        //class가 있는지 없는지 검증하기
        Clazz clazz = verifiedClazz(clazzId);
        Member member = findMember.findMemberByToken(loginUser);
        if (member.getRole() == Role.TEACHER) {
            if (clazz.getTeacher().getId() != member.getId()) {
                throw new BusinessLogicException(ExceptionCode.NOT_TEACHER);
            }
            clazz.setTeacher(null);
        } else if (member.getRole() == Role.PARENT) {
            if (clazz.getParent().getId() != member.getId()) {
                throw new BusinessLogicException(ExceptionCode.NOT_PARENT);
            }
            clazz.setParent(null);
        } else if (member.getRole() == Role.STUDENT) {
            if (clazz.getStudent().getId() != member.getId()) {
                throw new BusinessLogicException(ExceptionCode.NOT_STUDENT);
            }
            clazz.setStudent(null);
        }

        if (clazz.getParent() == null && clazz.getTeacher() == null && clazz.getStudent() == null) {
            // 완전 삭제
            clazzRepository.delete(clazz);
        } else clazzRepository.save(clazz);
    }

    public Clazz verifiedClazz(long clazzId) {
        Optional<Clazz> clazz = clazzRepository.findById(clazzId);
        return clazz.orElseThrow(() -> new BusinessLogicException(ExceptionCode.CLASS_NOT_FOUND));
    }

    @Transactional
    public Clazz updateClazz(Clazz clazz, ClazzRequestDto.Ids ids, LoginUser loginUser) {
        Clazz beforeClazz = verifiedClazz(clazz.getId());

        // Pay 방법이 바뀌는지 확인하기
        String beforePay = beforeClazz.getPay().getClass().getName();
        String afterPay = clazz.getPay().getClass().getName();
        if (beforePay.equals(afterPay)) { // 이전과 같은 Pay인 경우
            clazz.getPay().setId(beforeClazz.getPay().getId());
        } else { // Pay 방법이 바뀌는 경우
            // CurrentRound = 0 이 아닌 경우에는 에러 발생시키기
            // TODO : CurrentRound = 0 이 아닌 경우에는 팝업창 띄우기
            if (!checkCurrentRound(beforeClazz))
                throw new BusinessLogicException(ExceptionCode.NOT_CHANGE_PAY_TYPE);
        }

        // 수업일정이 바뀌었는지 확인하기
        for (ClazzTime clazzTime : clazz.getClazzTimes()) {
            clazzTime.setClazz(clazz);
        }

        // 그외의 정보 설정하기(settleDate, totalTime, currentRound)
        clazz.setSettleDate(beforeClazz.getSettleDate());
        clazz.getPay().setTotalTime(beforeClazz.getPay().getTotalTime());
        clazz.getPay().setCurrentRound(beforeClazz.getPay().getCurrentRound());

        // class 관련 member 연관관계 성립
        classSetMembers(ids, clazz);

        payRepository.save(clazz.getPay());
        return clazzRepository.save(clazz);
    }

    @Transactional
    public Clazz updateClazz(Clazz clazz) {
        return clazzRepository.save(clazz);
    }

    public Clazz updateClazz(long classId, boolean isDone) {
        Clazz clazz = verifiedClazz(classId);
        clazz.setIsDone(isDone);
        return clazzRepository.save(clazz);
    }

    public List<Clazz> getClazzes(LoginUser loginUser) {
        Member member = findMember.findMemberByToken(loginUser);
        return getClazzes(member);
    }

    public List<Clazz> getClazzes(Member member) {
        Role role = member.getRole();
        long memberId = member.getId();

        List<Clazz> clazz = null;

        switch (role) {
            case TEACHER -> clazz = clazzRepository.findByTeacherId(memberId);
            case STUDENT -> clazz = clazzRepository.findByStudentId(memberId);
            case PARENT -> clazz = clazzRepository.findByParentId(memberId);
        }

        return clazz;
    }

    // 정산 요청하기
    public Clazz settleRequestClazz(long clazzId) {
        Clazz clazz = verifiedClazz(clazzId);

        // Pay 방법에 따른 정산방법 분류
        if (clazz.getPay().getClass().getName().contains("FixedDatePay")) { // FixedDatePay
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
        } else if (clazz.getPay().getClass().getName().contains("RoundPay")) { // RoundPay
            RoundPay pay = (RoundPay) clazz.getPay();
            int round = pay.getRound();

            // 현재 round가 지정한 round보다 클 경우 정산 허용
            if (clazz.getPay().getCurrentRound() >= round) {
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
        if (clazz.getPay().getCurrentRound() == 0) return true; // 정산방법 바꾸기 OK
        return false; // 정산방법 바꾸기 X
    }

    // 클래스의 member 전부 변경하기
    public void classSetMembers(ClazzRequestDto.Ids ids, Clazz clazz) {
        if (ids.getTeacherId() != null) {
            Member teacher = memberService.verifiedMember(ids.getTeacherId());
            if (Role.TEACHER != teacher.getRole()) {
                throw new BusinessLogicException(ExceptionCode.NOT_TEACHER);
            }
            clazz.setTeacher(teacher);
        }
        if (ids.getParentId() != null) {
            Member parent = memberService.verifiedMember(ids.getParentId());
            if (Role.PARENT != parent.getRole()) {
                throw new BusinessLogicException(ExceptionCode.NOT_PARENT);
            }
            clazz.setParent(parent);
        }
        if (ids.getStudentId() != null) {
            Member student = memberService.verifiedMember(ids.getStudentId());
            if (Role.STUDENT != student.getRole()) {
                throw new BusinessLogicException(ExceptionCode.NOT_STUDENT);
            }
            clazz.setStudent(student);
        }
    }
}