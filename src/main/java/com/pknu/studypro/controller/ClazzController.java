package com.pknu.studypro.controller;

import com.pknu.studypro.controller.auth.Auth;
import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.dto.ClazzResponseDto;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
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

    // Create
    @PostMapping("/class")
    public ResponseEntity createClazz(@Auth LoginUser loginUser,
                                      @Valid @RequestBody ClazzRequestDto.Post post) {
        Clazz clazz = clazzMapper.clazzPostDtoToClazzCustom(post);
        clazz = clazzService.createClazz(clazz, post.getIds());
        ClazzResponseDto.Response response = clazzMapper.clazzToClazzResponseCustom(clazz);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 정산 요청하기
    @GetMapping("/class/settle-request/{classId}")
    public ResponseEntity settleClazzByTeacher(@Auth LoginUser loginUser,
                                               @Positive @PathVariable("classId") long clazzId) {
        if (loginUser.role() != Role.TEACHER) throw new BusinessLogicException(ExceptionCode.NOT_TEACHER); // 권한 확인
        Clazz clazz = clazzService.settleRequestClazz(clazzId);
        ClazzResponseDto.Response response = clazzMapper.clazzToClazzResponseCustom(clazz);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 정산 허용하기
    @GetMapping("/class/settle-allow/{classId}")
    public ResponseEntity settleClazzByParent(@Auth LoginUser loginUser,
                                              @Positive @PathVariable("classId") long clazzId) {
        if (loginUser.role() != Role.PARENT) throw new BusinessLogicException(ExceptionCode.NOT_PARENT); // 권한 확인
        Clazz clazz = clazzService.settleAllowClazz(clazzId);
        ClazzResponseDto.Response response = clazzMapper.clazzToClazzResponseCustom(clazz);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Delete
    @DeleteMapping("/class/{classId}")
    public ResponseEntity deleteClazz(@Auth LoginUser loginUser,
                                      @Positive @PathVariable("classId") long clazzId) {
        clazzService.deleteClazz(clazzId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/class/{classId}")
    public ResponseEntity putClazz(@Auth LoginUser loginUser,
                                   @Valid @RequestBody ClazzRequestDto.Post post,
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
    public ResponseEntity getTeacherClass(@Auth LoginUser loginUser,
                                          @RequestParam("memberId") long memberId) {
        List<Clazz> clazzes = clazzService.getClazz(memberId, Role.TEACHER);
        List<ClazzResponseDto.Response> responses = clazzMapper.clazzListToClazzResponseList(clazzes);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/class/student")
    public ResponseEntity getStudentClass(@Auth LoginUser loginUser,
                                          @RequestParam("memberId") long memberId) {
        List<Clazz> clazzes = clazzService.getClazz(memberId, Role.STUDENT);
        List<ClazzResponseDto.Response> responses = clazzMapper.clazzListToClazzResponseList(clazzes);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/class/parent")
    public ResponseEntity getParentClass(@Auth LoginUser loginUser,
                                         @RequestParam("memberId") long memberId) {
        List<Clazz> clazzes = clazzService.getClazz(memberId, Role.PARENT);
        List<ClazzResponseDto.Response> responses = clazzMapper.clazzListToClazzResponseList(clazzes);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}