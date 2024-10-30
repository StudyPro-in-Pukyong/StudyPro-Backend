package com.pknu.studypro.mapper;

import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.dto.MemberResponseDto;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberResponseDto.Response memberToMemberResponseDto(Member member);
    List<MemberResponseDto.Response> membersToMemberResponseDtos(List<Member> member);
}
