package com.pknu.studypro.mapper;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.clazz.Pay;
import com.pknu.studypro.domain.clazz.RoundPay;
import com.pknu.studypro.dto.ClazzRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClazzMapper {
    Clazz clazzPostDtoToClazz(ClazzRequestDto.Post post, Pay pay);
    FixedDatePay FIXED_DATE_PAY(ClazzRequestDto.PostPay pay);
    RoundPay ROUND_PAY(ClazzRequestDto.PostPay pay);

}
