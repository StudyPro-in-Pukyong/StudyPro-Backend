package com.pknu.studypro.service;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.Pay;
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

        return clazzRepository.save(clazz);
    }
}
