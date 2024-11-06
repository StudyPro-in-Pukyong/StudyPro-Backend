package com.pknu.studypro.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pknu.studypro.controller.auth.AuthArgumentResolver;
import com.pknu.studypro.controller.auth.KakaoTokenConverter;
import com.pknu.studypro.domain.Alert.Alert;
import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.clazz.Pay;
import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.AlertResponseDto;
import com.pknu.studypro.dto.MemberRequestDto;
import com.pknu.studypro.dto.MemberResponseDto;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.mapper.AlertMapper;
import com.pknu.studypro.mapper.MemberMapper;
import com.pknu.studypro.service.AlertService;
import com.pknu.studypro.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlertService alertService;
    @MockBean
    private AlertMapper alertMapper;
    @MockBean
    private AuthArgumentResolver authArgumentResolver;
    private Alert alert;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext,
               final RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        Member teacher = new Member(Role.TEACHER, LoginType.KAKAO, "teacher", null, "nickname", "email", "phoneNumber");
        Member student = new Member(Role.STUDENT, LoginType.KAKAO, "student", null, "nickname", "email", "phoneNumber");
        FixedDatePay pay = new FixedDatePay(10000, 1);
        Clazz clazz = new Clazz(1L, pay, "Math Class", "subject", teacher, null, student, null);
        alert = new Alert(teacher, student, clazz, 10000, LocalDate.now(), null);
    }

    @Test
    @DisplayName("알람 생성")
    void createAlert() throws Exception {
        // given
        AlertResponseDto.Response responseDto = new AlertResponseDto.Response(1L, "Math Class", 10000,
                LocalDate.now(), null, null);

        given(alertService.createAlert(any(LoginUser.class), anyLong())).willReturn(alert);
        given(alertMapper.alertToAlertResponseDto(alert)).willReturn(responseDto);

        // when, then
        mockMvc.perform(post("/alert")
                        .queryParam("clazzId", alert.getClazz().getId().toString())
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.classTitle").value("Math Class"))
                .andExpect(jsonPath("$.amount").value(10000))
                .andDo(document("알람 생성",
                        resource(ResourceSnippetParameters.builder()
                                .description("알람 생성")
                                .tag("Alert API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .queryParameters(
                                        parameterWithName("clazzId").description("알람을 생성할 클래스 ID")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("알람 ID"),
                                        fieldWithPath("classTitle").description("클래스 제목"),
                                        fieldWithPath("amount").description("월급"),
                                        fieldWithPath("requestDate").description("요청일"),
                                        fieldWithPath("acceptDate").description("수락일"),
                                        fieldWithPath("settleDate").description("마지막 월급일")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("알람 목록 조회")
    void getAlerts() throws Exception {
        // given
        List<AlertResponseDto.Response> responses = List.of(
                new AlertResponseDto.Response(1L, "Math Class", 10000, LocalDate.now(), null, null),
                new AlertResponseDto.Response(2L, "English Class", 10000, LocalDate.now(), null, null)
        );

        given(alertService.getAlerts(any(LoginUser.class))).willReturn(Collections.singletonList(alert));
        given(alertMapper.alertsToAlertResponseDtos(any())).willReturn(responses);

        // when, then
        mockMvc.perform(get("/alerts")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].classTitle").value("Math Class"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].classTitle").value("English Class"))
                .andDo(document("알람 목록 조회",
                        resource(ResourceSnippetParameters.builder()
                                .description("알람 목록 조회")
                                .tag("Alert API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .responseFields(
                                        fieldWithPath("[].id").description("알람 ID"),
                                        fieldWithPath("[].classTitle").description("클래스 제목"),
                                        fieldWithPath("[].amount").description("월급"),
                                        fieldWithPath("[].requestDate").description("요청일"),
                                        fieldWithPath("[].acceptDate").description("수락일"),
                                        fieldWithPath("[].settleDate").description("마지막 월급일")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("알람 수락")
    void acceptAlert() throws Exception {
        // given
        AlertResponseDto.Response responseDto = new AlertResponseDto.Response(1L, "Math Class", 10000,
                LocalDate.now(), LocalDate.now(), LocalDate.now());

        given(alertService.acceptAlert(any(LoginUser.class), anyLong())).willReturn(alert);
        given(alertMapper.alertToAlertResponseDto(alert)).willReturn(responseDto);

        // when, then
        mockMvc.perform(post("/alert/accept")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .queryParam("alertId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.classTitle").value("Math Class"))
                .andExpect(jsonPath("$.amount").value(10000))
                .andDo(document("알람 수락",
                        resource(ResourceSnippetParameters.builder()
                                .description("알람 수락")
                                .tag("Alert API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .queryParameters(
                                        parameterWithName("alertId").description("수락할 알람 ID")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("알람 ID"),
                                        fieldWithPath("classTitle").description("클래스 제목"),
                                        fieldWithPath("amount").description("월급"),
                                        fieldWithPath("requestDate").description("요청일"),
                                        fieldWithPath("acceptDate").description("수락일"),
                                        fieldWithPath("settleDate").description("마지막 월급일")
                                ).build()
                        )
                ));
    }
}