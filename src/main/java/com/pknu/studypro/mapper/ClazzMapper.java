package com.pknu.studypro.mapper;

import com.pknu.studypro.domain.clazz.*;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.dto.ClazzResponseDto;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClazzMapper {
    @Mapping(source = "post.id", target = "id")
    Clazz clazzPostDtoToClazz(ClazzRequestDto.Post post, Pay pay);

    default Clazz clazzPostDtoToClazzCustom(ClazzRequestDto.Post post) {
        if (post.isFixedDatePay()) return clazzPostDtoToClazz(post, FIXED_DATE_PAY(post.getPostPay()));
        else if (post.isRoundPay()) {
            RoundPay roundPay = ROUND_PAY(post.getPostPay());
            return clazzPostDtoToClazz(post, roundPay);
        } else throw new BusinessLogicException(ExceptionCode.CLASS_POST_FAIL);
    }

    FixedDatePay FIXED_DATE_PAY(ClazzRequestDto.PostPay pay);

    RoundPay ROUND_PAY(ClazzRequestDto.PostPay pay);

    ClazzResponseDto.Response clazzToClazzResponse(Clazz clazz);

    default ClazzResponseDto.Response clazzToClazzResponseCustom(Clazz clazz) {
        ClazzResponseDto.Response response = clazzToClazzResponse(clazz);
        response.setIsDone(clazz.isDone());

        if (clazz.getPay().getClass().getName().contains("FixedDatePay"))
            response.setResponsePay(RESPONSE_FIXED_DATE_PAY(clazz));
        else if (clazz.getPay().getClass().getName().contains("RoundPay"))
            response.setResponsePay(ROUND_PAY(clazz));

        response.setClazzTimes(clazzTimesToClazzResponseDtoClazzTimes(clazz.getClazzTimes()));

        ClazzResponseDto.Ids ids = new ClazzResponseDto.Ids(
                clazz.getTeacher() == null ? null : clazz.getTeacher().getId(),
                clazz.getParent() == null ? null : clazz.getParent().getId(),
                clazz.getStudent() == null ? null : clazz.getStudent().getId()
        );
        response.setIds(ids);

        return response;
    }

    // 수업일정 설정
    default List<ClazzResponseDto.ClazzTime> clazzTimesToClazzResponseDtoClazzTimes(List<ClazzTime> clazzTimes) {
        List<ClazzResponseDto.ClazzTime> clazzTimesResponses = new ArrayList<>();
        for (ClazzTime clazzTime : clazzTimes) {
            ClazzResponseDto.ClazzTime clazzTimeResonse = new ClazzResponseDto.ClazzTime(
                    clazzTime.getId(),
                    clazzTime.getClazzDate(),
                    clazzTime.getStartTime(),
                    clazzTime.getEndTime()
            );
            clazzTimesResponses.add(clazzTimeResonse);
        }

        return clazzTimesResponses;
    }

    default List<ClazzResponseDto.Response> clazzListToClazzResponseList(List<Clazz> clazzes) {
        List<ClazzResponseDto.Response> responses = new ArrayList<>();
        for(Clazz clazz : clazzes) {
            responses.add(clazzToClazzResponseCustom(clazz));
        }

        return responses;
    }

    default ClazzResponseDto.ResponseFixedDatePay RESPONSE_FIXED_DATE_PAY(Clazz clazz) {
        FixedDatePay pay = (FixedDatePay) clazz.getPay();
        return new ClazzResponseDto.ResponseFixedDatePay(
                pay.getId(),
                pay.getAmount(),
                pay.getTotalTime(),
                pay.getCurrentRound(),
                pay.getDate()
        );
    }

    default ClazzResponseDto.ResponseRoundPay ROUND_PAY(Clazz clazz) {
        RoundPay pay = (RoundPay) clazz.getPay();
        return new ClazzResponseDto.ResponseRoundPay(
                pay.getId(),
                pay.getAmount(),
                pay.getTotalTime(),
                pay.getCurrentRound(),
                pay.getRound()
        );
    }
}
