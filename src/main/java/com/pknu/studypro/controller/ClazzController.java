package com.pknu.studypro.controller;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.dto.LessonResponseDto;
import com.pknu.studypro.mapper.ClazzMapper;
import com.pknu.studypro.repository.ClazzRepository;
import com.pknu.studypro.service.ClazzService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class ClazzController {

    private final ClazzService clazzService;
    private final ClazzMapper clazzMapper;

    @PostMapping("/class")
    public ResponseEntity createClazz(@Valid @RequestBody ClazzRequestDto.Post post){
        Clazz clazz = null;
        if(post.isFixedDatePay())  clazz = clazzMapper.clazzPostDtoToClazz(post, clazzMapper.FIXED_DATE_PAY(post.getPostPay()));
        else if(post.isRoundPay())  clazz = clazzMapper.clazzPostDtoToClazz(post, clazzMapper.ROUND_PAY(post.getPostPay()));
        clazz = clazzService.createClazz(clazz, post.getIds());

        return new ResponseEntity<>(clazz, HttpStatus.OK);
    }

    @DeleteMapping("/class/{classId}")
    public ResponseEntity deleteClazz(@Positive @PathVariable("classId") long clazzId) {
        clazzService.deleteClazz(clazzId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/class/{classId}")
    public ResponseEntity putClazz(@Valid @RequestBody ClazzRequestDto.Post post,
                                   @Positive @PathVariable("classId") long clazzId) {
        Clazz clazz = null;
        if(post.isFixedDatePay()) clazz = clazzMapper.clazzPostDtoToClazz(post, clazzMapper.FIXED_DATE_PAY(post.getPostPay()));
        else if(post.isRoundPay()) clazz = clazzMapper.clazzPostDtoToClazz(post, clazzMapper.ROUND_PAY(post.getPostPay()));
        clazz = clazzService.updateClazz(clazz, post.getIds(), clazzId);

        return new ResponseEntity<>(clazz, HttpStatus.OK);
    }

    //클래스 조회 선생님, 학부모, 학생
    //URL을 나눴기 때문에 추가적으로 검증할 필요 없음
    @GetMapping("/class/teacher")
    public ResponseEntity getClass(@RequestParam("memberId") long memberId) {
        List<Clazz> clazz = clazzService.getClazz(memberId);

        return new ResponseEntity<>(clazz, HttpStatus.OK);
    }
}