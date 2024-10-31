package com.pknu.studypro.controller;

import com.pknu.studypro.controller.auth.Auth;
import com.pknu.studypro.domain.Alert.Alert;
import com.pknu.studypro.dto.AlertResponseDto;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.mapper.AlertMapper;
import com.pknu.studypro.service.AlertService;
import com.pknu.studypro.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Validated
@AllArgsConstructor
public class AlertController {
    private final MemberService memberService;
    private final AlertService alertService;
    private final AlertMapper alertMapper;

    @GetMapping("/alert")
    public String alert() {
        return "alert";
    }

    // 월급 요청시 알람 생성
    @PostMapping("/alert")
    public ResponseEntity createAlert(@Auth final LoginUser loginUser,
                                      @RequestParam final long clazzId) {
        Alert alert = alertService.createAlert(loginUser, clazzId);
        AlertResponseDto.Response response = alertMapper.alertToAlertResponseDto(alert);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 알람 조회
    @GetMapping("/alerts")
    public ResponseEntity getAlerts(@Auth final LoginUser loginUser) {
        List<Alert> alerts = alertService.getAlerts(loginUser);
        List<AlertResponseDto.Response> responses = alertMapper.alertsToAlertResponseDtos(alerts);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // 수락
    @PostMapping("/alert/accept")
    public ResponseEntity getAlerts(@Auth final LoginUser loginUser,
                                    @RequestParam final long alertId) {
        Alert alert = alertService.acceptAlert(loginUser, alertId);
        AlertResponseDto.Response response = alertMapper.alertToAlertResponseDto(alert);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}