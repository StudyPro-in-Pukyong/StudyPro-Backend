package com.pknu.studypro.mapper;

import com.pknu.studypro.domain.Alert.Alert;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.dto.AlertResponseDto;
import com.pknu.studypro.dto.MemberResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlertMapper {
    @Mapping(source = "alert.clazz.title", target = "classTitle")
    AlertResponseDto.Response alertToAlertResponseDto(Alert alert);
    @Mapping(source = "alert.clazz.title", target = "classTitle")
    List<AlertResponseDto.Response> alertsToAlertResponseDtos(List<Alert> alerts);
}
