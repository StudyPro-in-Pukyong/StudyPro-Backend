package com.pknu.studypro.mapper;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.clazz.Pay;
import com.pknu.studypro.domain.clazz.RoundPay;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.dto.ClazzResponseDto;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClazzMapper {
    Clazz clazzPostDtoToClazz(ClazzRequestDto.Post post, Pay pay);
    default Clazz clazzPostDtoToClazzCustom(ClazzRequestDto.Post post) {
        if(post.isFixedDatePay())  return clazzPostDtoToClazz(post, FIXED_DATE_PAY(post.getPostPay()));
        else if(post.isRoundPay())  return clazzPostDtoToClazz(post, ROUND_PAY(post.getPostPay()));
        else throw new BusinessLogicException(ExceptionCode.CLASS_POST_FAIL);
    }
    FixedDatePay FIXED_DATE_PAY(ClazzRequestDto.PostPay pay);
    RoundPay ROUND_PAY(ClazzRequestDto.PostPay pay);

    ClazzResponseDto.Response clazzToClazzResponse(Clazz clazz);
    default ClazzResponseDto.Response clazzToClazzResponseCustom(Clazz clazz) {
        ClazzResponseDto.Response response = clazzToClazzResponse(clazz);

        if(clazz.getPay().getClass().getName().contains("FixedDatePay"))
            response.setResponsePay(RESPONSE_FIXED_DATE_PAY(clazz));
        else if(clazz.getPay().getClass().getName().contains("RoundPay"))
            response.setResponsePay(ROUND_PAY(clazz));

        return response;
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
