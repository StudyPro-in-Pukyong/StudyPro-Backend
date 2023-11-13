package com.pknu.studypro.controller;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.RoundPay;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.dto.ClazzResponseDto;
import com.pknu.studypro.mapper.ClazzMapper;
import com.pknu.studypro.service.ClazzService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class ClazzController {

    private final ClazzService clazzService;
    private final ClazzMapper clazzMapper;

    @PostMapping("/class")
    public ResponseEntity createClazz(@Valid @RequestBody ClazzRequestDto.Post post){
        Clazz clazz = clazzMapper.clazzPostDtoToClazzCustom(post);
        clazz = clazzService.createClazz(clazz, post.getIds());
        ClazzResponseDto.Response response = clazzMapper.clazzToClazzResponseCustom(clazz);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/class/{classId}")
    public ResponseEntity deleteClazz(@Positive @PathVariable("classId") long clazzId) {
        clazzService.deleteClazz(clazzId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/class/{classId}")
    public ResponseEntity putClazz(@Valid @RequestBody ClazzRequestDto.Post post,
                                   @Positive @PathVariable("classId") long clazzId) {
        Clazz clazz = clazzMapper.clazzPostDtoToClazzCustom(post);

//        RoundPay roundPay = (RoundPay) (clazz.getPay());
//        System.out.println("!! controller : " + roundPay.getRound());

        clazz.setClassId(clazzId);
        clazz = clazzService.updateClazz(clazz, post.getIds());
        ClazzResponseDto.Response response = clazzMapper.clazzToClazzResponseCustom(clazz);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //클래스 조회 선생님, 학부모, 학생
    //URL을 나눴기 때문에 추가적으로 검증할 필요 없음
    @GetMapping("/class/teacher")
    public ResponseEntity getClass(@RequestParam("memberId") long memberId) {
        List<Clazz> clazzes = clazzService.getClazz(memberId);
        List<ClazzResponseDto.Response> responses = clazzMapper.clazzListToClazzResponseList(clazzes);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}