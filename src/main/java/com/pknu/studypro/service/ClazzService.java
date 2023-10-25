package com.pknu.studypro.service;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.Pay;
import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.repository.ClazzRepository;
import com.pknu.studypro.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClazzService {

    private final ClazzRepository clazzRepository;
    private final MemberRepository memberRepository;

    public Clazz createClazz(Clazz clazz, ClazzRequestDto.Ids ids) {
//        Member teacher = new Member(Role.TEACHER, LoginType.KAKAO, "선생님", null, "닉네임1");
//        memberRepository.save(teacher);

        // ------------------------------------------------------
        // memberId 식별하는 코드 작성하기
        // member들이 one to one 매핑되어 있어서 같은 memberId 등록 X -> 수정 필요해보임
        clazz.setTeacher(memberRepository.findById(ids.getTeacherId()).get());
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
}
