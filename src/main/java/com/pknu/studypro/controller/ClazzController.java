package com.pknu.studypro.controller;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.mapper.ClazzMapper;
import com.pknu.studypro.service.ClazzService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Validated
public class ClazzController {

    private final ClazzService clazzService;
    private final ClazzMapper clazzMapper;

    @PostMapping("/class")
    public ResponseEntity createClazz(@Valid @RequestBody ClazzRequestDto.Post post){
        Clazz clazz = null;
        if(post.isFixedDatePay()) clazz = clazzMapper.clazzPostDtoToClazz(post, clazzMapper.FIXED_DATE_PAY(post.getPostPay()));
        else if(post.isRoundPay()) clazz = clazzMapper.clazzPostDtoToClazz(post, clazzMapper.ROUND_PAY(post.getPostPay()));
        clazz = clazzService.createClazz(clazz, post.getIds());

        return new ResponseEntity<>(clazz, HttpStatus.OK);
    }
}