package com.pknu.studypro.controller;

import com.pknu.studypro.controller.auth.Auth;
import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.RoundPay;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Validated
public class ClazzController {
    private final ClazzService clazzService;
    private final ClazzMapper clazzMapper;

    @Value("${firebase.apiKey}")
    private String apiKey;

    @Value("${firebase.authDomain}")
    private String authDomain;

    @Value("${firebase.projectId}")
    private String projectId;

    @Value("${firebase.storageBucket}")
    private String storageBucket;

    @Value("${firebase.messagingSenderId}")
    private String messagingSenderId;

    @Value("${firebase.appId}")
    private String appId;

    @Value("${firebase.measurementId}")
    private String measurementId;

    @Value("${firebase.vapidKey}")
    private String vapidKey;

    // Create Clazz 화면 전환
    @GetMapping("/createClazz")
    public String createClazz(Model model) {
        return "createClazz"; // createClazzInfo.html로 이동
    }

    // Update Clazz 화면 전환
    @GetMapping("/updateClazz")
    public String updateClazz(Model model) {
        return "updateClazz"; // createClazzInfo.html로 이동
    }

    // Clazz 화면 전환
    @GetMapping("/clazz")
    public String clazz(Model model) {
        // firebase config
        model.addAttribute("apiKey", apiKey);
        model.addAttribute("authDomain", authDomain);
        model.addAttribute("projectId", projectId);
        model.addAttribute("storageBucket", storageBucket);
        model.addAttribute("messagingSenderId", messagingSenderId);
        model.addAttribute("appId", appId);
        model.addAttribute("measurementId", messagingSenderId);
        model.addAttribute("vapidKey", vapidKey);

        return "clazz"; // clazz.html로 이동
    }

    // Create
    @PostMapping("/class")
    public String createClazz(@Auth LoginUser loginUser,
                              @Valid @RequestBody ClazzRequestDto.Post post) {
        // 클래스 생성
        Clazz clazz = clazzMapper.clazzPostDtoToClazzCustom(post);
        clazz = clazzService.createClazz(clazz, post.getIds(), loginUser);

        // 클래스 조회
        return "redirect:/clazz";
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

    //클래스 조회 선생님, 학부모, 학생
    @GetMapping("/class")
    public ResponseEntity getClasses(@Auth LoginUser loginUser) {
        List<Clazz> clazzes = clazzService.getClazzes(loginUser);
        List<ClazzResponseDto.Response> responses = clazzMapper.clazzListToClazzResponseList(clazzes);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    //클래스 조회 선생님, 학부모, 학생
    @GetMapping("/class/{classId}")
    public ResponseEntity getClass(@Positive @PathVariable("classId") long clazzId) {
        Clazz clazz = clazzService.verifiedClazz(clazzId);
        ClazzResponseDto.Response response = clazzMapper.clazzToClazzResponseCustom(clazz);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 클래스 수정
    @PutMapping("/class")
    public ResponseEntity putClazz(@Auth LoginUser loginUser,
                                   @Valid @RequestBody ClazzRequestDto.Post post) {
        Clazz clazz = clazzMapper.clazzPostDtoToClazzCustom(post);
        clazz = clazzService.updateClazz(clazz, post.getIds(), loginUser);
        ClazzResponseDto.Response response = clazzMapper.clazzToClazzResponseCustom(clazz);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 종료 여부 수정
    @PutMapping("/class/{classId}")
    public ResponseEntity putIsDoneClazz(@Auth LoginUser loginUser,
                                         @Positive @PathVariable("classId") long classId,
                                         @RequestParam("isDone") boolean isDone) {
        Clazz clazz = clazzService.updateClazz(classId, isDone);
        ClazzResponseDto.Response response = clazzMapper.clazzToClazzResponseCustom(clazz);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Delete
    @DeleteMapping("/class/{classId}")
    public ResponseEntity deleteClazz(@Auth LoginUser loginUser,
                                      @Positive @PathVariable("classId") long clazzId) {
        clazzService.deleteClazz(clazzId, loginUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}