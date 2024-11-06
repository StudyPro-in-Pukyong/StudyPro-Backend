package com.pknu.studypro.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pknu.studypro.controller.auth.AuthArgumentResolver;
import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.ClazzDate;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.ClazzRequestDto;
import com.pknu.studypro.dto.ClazzResponseDto;
import com.pknu.studypro.dto.LessonResponseDto;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.mapper.ClazzMapper;
import com.pknu.studypro.service.ClazzService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ClazzControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClazzService clazzService;
    @MockBean
    private ClazzMapper clazzMapper;
    @MockBean
    private AuthArgumentResolver authArgumentResolver;
    private Clazz clazz;
    private List<Clazz> clazzes;
    private ClazzResponseDto.Response response;
    private List<ClazzResponseDto.Response> responses;

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
        Clazz clazz2 = new Clazz(2L, pay, "Math Class", "subject", teacher, null, student, null);
        Clazz clazz3 = new Clazz(3L, pay, "Math Class", "subject", teacher, null, student, null);
        clazzes = List.of(clazz, clazz2, clazz3);

        response = new ClazzResponseDto.Response();
        response.setId(1L);
        response.setTitle("Math Class");
        response.setSubject("Mathematics");
        response.setIsDone(false);
        response.setResponsePay(new ClazzResponseDto.ResponseFixedDatePay(1L, 10000, 60, 1, 1));
        response.setIds(new ClazzResponseDto.Ids(1L, 2L, 3L));
        response.setClazzTimes(List.of(new ClazzResponseDto.ClazzTime(1L, ClazzDate.THU, LocalTime.of(0, 0), LocalTime.of(1, 0)),
                new ClazzResponseDto.ClazzTime(2L, ClazzDate.THU, LocalTime.of(0, 0), LocalTime.of(1, 0)
                )));

        ClazzResponseDto.Response response2 = new ClazzResponseDto.Response();
        response2.setId(2L);
        response2.setTitle("Korean Class");
        response2.setSubject("Korean");
        response2.setIsDone(false);
        response2.setResponsePay(new ClazzResponseDto.ResponseRoundPay(2L, 10000, 60, 1, 1));
        response2.setIds(new ClazzResponseDto.Ids(1L, 2L, 3L));
        response2.setClazzTimes(List.of(new ClazzResponseDto.ClazzTime(3L, ClazzDate.THU, LocalTime.of(0, 0), LocalTime.of(1, 0))));

        ClazzResponseDto.Response response3 = new ClazzResponseDto.Response();
        response3.setId(3L);
        response3.setTitle("English Class");
        response3.setSubject("English");
        response3.setIsDone(false);
        response3.setResponsePay(new ClazzResponseDto.ResponseRoundPay(3L, 10000, 60, 1, 1));
        response3.setIds(new ClazzResponseDto.Ids(1L, 2L, 3L));
        response3.setClazzTimes(List.of(new ClazzResponseDto.ClazzTime(4L, ClazzDate.THU, LocalTime.of(0, 0), LocalTime.of(1, 0))));

        responses = List.of(response, response2, response3);
    }

//    @Test
//    @DisplayName("정산 요청")
//    void settleClazzByTeacher() throws Exception {
//        // given
//        given(authArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(new LoginUser("teacher", Role.TEACHER));
//        given(clazzService.settleRequestClazz(anyLong())).willReturn(clazz);
//        given(clazzMapper.clazzToClazzResponseCustom(clazz)).willReturn(response);
//
//        // when, then
//        mockMvc.perform(get("/class/settle-request/{classId}", 1L)
//                        .header(AUTHORIZATION, "Bearer access.token")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andDo(document("정산 요청",
//                        resource(ResourceSnippetParameters.builder()
//                                .description("정산 요청")
//                                .tag("Clazz API")
//                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
//                                .pathParameters(parameterWithName("classId").description("정산할 클래스 ID"))
//                                .responseFields(
//                                        fieldWithPath("id").description("클래스 ID"),
//                                        fieldWithPath("title").description("클래스 이름"),
//                                        fieldWithPath("subject").description("과목명"),
//                                        fieldWithPath("isDone").description("클래스 종료 여부"),
//                                        fieldWithPath("PostPay").description("지급 방식 및 급여"),
//                                        fieldWithPath("postPay.amount").description("급여 금액 (회당 금액 or 월마다 금액)"),
//                                        fieldWithPath("postPay.date").description("급여 지급일 (정기적으로 설정할 경우, 회차 수당일 경우에는 null)").optional(),
//                                        fieldWithPath("postPay.round").description("회차 수당 (회차 기반 지급일 경우, 급여 지급일일 경우에는 null)").optional(),
//                                        fieldWithPath("ids").description("관련 사람들 ID"),
//                                        fieldWithPath("ids.teacherId").description("선생님 ID"),
//                                        fieldWithPath("ids.parentId").description("부모님 ID"),
//                                        fieldWithPath("ids.studentId").description("학생 ID"),
//                                        fieldWithPath("clazzTimes").description("클래스의 수업 일정"),
//                                        subsectionWithPath("clazzTimes[].id").description("수업 일정의 식별자").optional(),
//                                        subsectionWithPath("clazzTimes[].clazzDate").description("수업 요일(MON, TUE, WED, THU, FRI, SAT, SUN)"),
//                                        subsectionWithPath("clazzTimes[].startTime").description("수업 시작 시간"),
//                                        subsectionWithPath("clazzTimes[].endTime").description("수업 종료 시간")
//                                ).build()
//                        )
//                ));
//    }
//
//    @Test
//    @DisplayName("정산 허용")
//    void settleClazzByParent() throws Exception {
//        // given
//        given(clazzService.settleAllowClazz(anyLong())).willReturn(clazz);
//        given(clazzMapper.clazzToClazzResponseCustom(clazz)).willReturn(response);
//
//        // when, then
//        mockMvc.perform(get("/class/settle-allow/{classId}", 1L)
//                        .header(AUTHORIZATION, "Bearer access.token")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andDo(document("정산 허용",
//                        resource(ResourceSnippetParameters.builder()
//                                .description("정산 허용")
//                                .tag("Clazz API")
//                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
//                                .pathParameters(parameterWithName("classId").description("정산할 클래스 ID"))
//                                .responseFields(
//                                        fieldWithPath("id").description("클래스 ID"),
//                                        fieldWithPath("title").description("클래스 이름"),
//                                        fieldWithPath("subject").description("과목명"),
//                                        fieldWithPath("isDone").description("클래스 종료 여부"),
//                                        fieldWithPath("PostPay").description("지급 방식 및 급여"),
//                                        fieldWithPath("postPay.amount").description("급여 금액 (회당 금액 or 월마다 금액)"),
//                                        fieldWithPath("postPay.date").description("급여 지급일 (정기적으로 설정할 경우, 회차 수당일 경우에는 null)").optional(),
//                                        fieldWithPath("postPay.round").description("회차 수당 (회차 기반 지급일 경우, 급여 지급일일 경우에는 null)").optional(),
//                                        fieldWithPath("ids").description("관련 사람들 ID"),
//                                        fieldWithPath("ids.teacherId").description("선생님 ID"),
//                                        fieldWithPath("ids.parentId").description("부모님 ID"),
//                                        fieldWithPath("ids.studentId").description("학생 ID"),
//                                        fieldWithPath("clazzTimes").description("클래스의 수업 일정"),
//                                        subsectionWithPath("clazzTimes[].id").description("수업 일정의 식별자").optional(),
//                                        subsectionWithPath("clazzTimes[].clazzDate").description("수업 요일(MON, TUE, WED, THU, FRI, SAT, SUN)"),
//                                        subsectionWithPath("clazzTimes[].startTime").description("수업 시작 시간"),
//                                        subsectionWithPath("clazzTimes[].endTime").description("수업 종료 시간")
//                                ).build()
//                        )
//                ));
//    }

    @Test
    @DisplayName("토큰을 통한 클래스 목록 조회")
    void getClasses() throws Exception {
        // given
        given(clazzService.getClazzes(any(LoginUser.class))).willReturn(clazzes);
        given(clazzMapper.clazzListToClazzResponseList(clazzes)).willReturn(responses);

        // when, then
        mockMvc.perform(get("/class")
                        .header(AUTHORIZATION, "Bearer access.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("토큰을 통한 클래스 목록 조회",
                        resource(ResourceSnippetParameters.builder()
                                .description("토큰을 통한 클래스 목록 조회")
                                .tag("Clazz API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .responseFields(
                                        fieldWithPath("[].id").description("수정할 클래스 ID"),
                                        fieldWithPath("[].title").description("클래스 이름"),
                                        fieldWithPath("[].subject").description("과목명"),
                                        fieldWithPath("[].isDone").description("클래스 종료 여부"),
                                        // responsePay 구조
                                        fieldWithPath("[].responsePay.id").description("지급 방식 ID"),
                                        fieldWithPath("[].responsePay.amount").description("급여 금액 (회당 금액 또는 월마다 지급 금액)"),
                                        fieldWithPath("[].responsePay.totalTime").description("총 수업 시간"),
                                        fieldWithPath("[].responsePay.currentRound").description("현재 회차"),
                                        // ResponseFixedDatePay 구조
                                        fieldWithPath("[].responsePay.date")
                                                .type(JsonFieldType.NUMBER)
                                                .optional()
                                                .description("급여 지급일 (정기적으로 설정된 경우, 회차 수당일 경우에는 null)"),
                                        // ResponseRoundPay 구조 (type 명시)
                                        fieldWithPath("[].responsePay.round")
                                                .type(JsonFieldType.NUMBER)
                                                .optional()
                                                .description("회차 수당 (회차 기반 지급일 경우, 급여 지급일일 경우에는 null)"),

                                        // ids 구조
                                        fieldWithPath("[].ids").description("관련 사람들 ID"),
                                        fieldWithPath("[].ids.teacherId").description("선생님 ID"),
                                        fieldWithPath("[].ids.parentId").description("부모님 ID"),
                                        fieldWithPath("[].ids.studentId").description("학생 ID"),

                                        // clazzTimes 구조
                                        fieldWithPath("[].clazzTimes").description("클래스의 수업 일정"),
                                        subsectionWithPath("[].clazzTimes[].id").description("수업 일정의 식별자").optional(),
                                        subsectionWithPath("[].clazzTimes[].clazzDate").description("수업 요일(MON, TUE, WED, THU, FRI, SAT, SUN)"),
                                        subsectionWithPath("[].clazzTimes[].startTime").description("수업 시작 시간"),
                                        subsectionWithPath("[].clazzTimes[].endTime").description("수업 종료 시간")
                                ).build()
                        )));
    }

    @Test
    @DisplayName("클래스ID를 통한 클래스 조회")
    void getClazz() throws Exception {
        // given
        given(clazzService.verifiedClazz(anyLong())).willReturn(clazz);
        given(clazzMapper.clazzToClazzResponseCustom(clazz)).willReturn(response);

        // when, then
        mockMvc.perform(get("/class/{classId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("클래스ID를 통한 클래스 조회",
                        resource(ResourceSnippetParameters.builder()
                                .description("클래스ID를 통한 클래스 조회")
                                .tag("Clazz API")
                                .pathParameters(
                                        parameterWithName("classId").description("조회할 클래스 ID")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("수정할 클래스 ID"),
                                        fieldWithPath("title").description("클래스 이름"),
                                        fieldWithPath("subject").description("과목명"),
                                        fieldWithPath("isDone").description("클래스 종료 여부"),
                                        // responsePay 구조
                                        fieldWithPath("responsePay.id").description("지급 방식 ID"),
                                        fieldWithPath("responsePay.amount").description("급여 금액 (회당 금액 또는 월마다 지급 금액)"),
                                        fieldWithPath("responsePay.totalTime").description("총 수업 시간"),
                                        fieldWithPath("responsePay.currentRound").description("현재 회차"),
                                        // ResponseFixedDatePay 구조
                                        fieldWithPath("responsePay.date")
                                                .type(JsonFieldType.NUMBER)
                                                .optional()
                                                .description("급여 지급일 (정기적으로 설정된 경우, 회차 수당일 경우에는 null)"),
                                        // ResponseRoundPay 구조 (type 명시)
                                        fieldWithPath("responsePay.round")
                                                .type(JsonFieldType.NUMBER)
                                                .optional()
                                                .description("회차 수당 (회차 기반 지급일 경우, 급여 지급일일 경우에는 null)"),

                                        // ids 구조
                                        fieldWithPath("ids").description("관련 사람들 ID"),
                                        fieldWithPath("ids.teacherId").description("선생님 ID"),
                                        fieldWithPath("ids.parentId").description("부모님 ID"),
                                        fieldWithPath("ids.studentId").description("학생 ID"),

                                        // clazzTimes 구조
                                        fieldWithPath("clazzTimes").description("클래스의 수업 일정"),
                                        subsectionWithPath("clazzTimes[].id").description("수업 일정의 식별자").optional(),
                                        subsectionWithPath("clazzTimes[].clazzDate").description("수업 요일(MON, TUE, WED, THU, FRI, SAT, SUN)"),
                                        subsectionWithPath("clazzTimes[].startTime").description("수업 시작 시간"),
                                        subsectionWithPath("clazzTimes[].endTime").description("수업 종료 시간")
                                ).build()
                        )));
    }

    @Test
    @DisplayName("클래스 업데이트")
    void updateClazz() throws Exception {
        // given
        ClazzRequestDto.Post postDto = new ClazzRequestDto.Post();
        postDto.setId(1L);
        postDto.setTitle("Updated Math Class");
        postDto.setSubject("Advanced Mathematics");
        postDto.setIsDone(false);
        postDto.setPostPay(new ClazzRequestDto.PostPay(
                10000, 2, 10
        ));
        postDto.setIds(new ClazzRequestDto.Ids(
                1L, 2L, 3L
        ));
        ClazzRequestDto.ClazzTime clazzTime1 = new ClazzRequestDto.ClazzTime(
                1L, ClazzDate.MON, LocalTime.of(0, 0), LocalTime.of(1, 0)
        );
        ClazzRequestDto.ClazzTime clazzTime2 = new ClazzRequestDto.ClazzTime(
                2L, ClazzDate.THU, LocalTime.of(0, 0), LocalTime.of(1, 0)
        );
        postDto.setClazzTimes(List.of(clazzTime1, clazzTime2));

        given(clazzMapper.clazzPostDtoToClazzCustom(postDto)).willReturn(clazz);
        given(clazzService.updateClazz(any(Clazz.class), any(ClazzRequestDto.Ids.class), any(LoginUser.class))).willReturn(clazz);
        given(clazzMapper.clazzToClazzResponseCustom(clazz)).willReturn(response);

        // when, then
        mockMvc.perform(put("/class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer access.token")
                        .content(objectMapper.writeValueAsString(postDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("클래스 업데이트",
                        resource(ResourceSnippetParameters.builder()
                                        .description("클래스 업데이트")
                                        .tag("Clazz API")
                                        .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                        .requestFields(
                                                fieldWithPath("id").description("수정할 클래스 ID"),
                                                fieldWithPath("title").description("클래스 이름"),
                                                fieldWithPath("subject").description("과목명"),
                                                fieldWithPath("isDone").description("클래스 종료 여부"),
                                                fieldWithPath("postPay").description("지급 방식 및 급여"),
                                                fieldWithPath("postPay.amount").description("급여 금액 (회당 금액 or 월마다 금액)"),
                                                fieldWithPath("postPay.date").description("급여 지급일 (정기적으로 설정할 경우, 회차 수당일 경우에는 null)").optional(),
                                                fieldWithPath("postPay.round").description("회차 수당 (회차 기반 지급일 경우, 급여 지급일일 경우에는 null)").optional(),
                                                fieldWithPath("ids").description("관련 사람들 ID"),
                                                fieldWithPath("ids.teacherId").description("선생님 ID"),
                                                fieldWithPath("ids.parentId").description("부모님 ID"),
                                                fieldWithPath("ids.studentId").description("학생 ID"),
                                                fieldWithPath("clazzTimes").description("클래스의 수업 일정"),
                                                subsectionWithPath("clazzTimes[].id").description("수업 일정의 식별자").optional(),
                                                subsectionWithPath("clazzTimes[].clazzDate").description("수업 요일(MON, TUE, WED, THU, FRI, SAT, SUN)"),
                                                subsectionWithPath("clazzTimes[].startTime").description("수업 시작 시간"),
                                                subsectionWithPath("clazzTimes[].endTime").description("수업 종료 시간")

                                                // `roundPay`와 `fixedDatePay` 무시
//                                                subsectionWithPath("roundPay").ignored(),
//                                                subsectionWithPath("fixedDatePay").ignored()
                                        )
                                        .responseFields(
                                                fieldWithPath("id").description("수정할 클래스 ID"),
                                                fieldWithPath("title").description("클래스 이름"),
                                                fieldWithPath("subject").description("과목명"),
                                                fieldWithPath("isDone").description("클래스 종료 여부"),
                                                // responsePay 구조
                                                fieldWithPath("responsePay.id").description("지급 방식 ID"),
                                                fieldWithPath("responsePay.amount").description("급여 금액 (회당 금액 또는 월마다 지급 금액)"),
                                                fieldWithPath("responsePay.totalTime").description("총 수업 시간"),
                                                fieldWithPath("responsePay.currentRound").description("현재 회차"),
                                                // ResponseFixedDatePay 구조
                                                fieldWithPath("responsePay.date")
                                                        .type(JsonFieldType.NUMBER)
                                                        .optional()
                                                        .description("급여 지급일 (정기적으로 설정된 경우, 회차 수당일 경우에는 null)"),
                                                // ResponseRoundPay 구조 (type 명시)
                                                fieldWithPath("responsePay.round")
                                                        .type(JsonFieldType.NUMBER)
                                                        .optional()
                                                        .description("회차 수당 (회차 기반 지급일 경우, 급여 지급일일 경우에는 null)"),

                                                // ids 구조
                                                fieldWithPath("ids").description("관련 사람들 ID"),
                                                fieldWithPath("ids.teacherId").description("선생님 ID"),
                                                fieldWithPath("ids.parentId").description("부모님 ID"),
                                                fieldWithPath("ids.studentId").description("학생 ID"),

                                                // clazzTimes 구조
                                                fieldWithPath("clazzTimes").description("클래스의 수업 일정"),
                                                subsectionWithPath("clazzTimes[].id").description("수업 일정의 식별자").optional(),
                                                subsectionWithPath("clazzTimes[].clazzDate").description("수업 요일(MON, TUE, WED, THU, FRI, SAT, SUN)"),
                                                subsectionWithPath("clazzTimes[].startTime").description("수업 시작 시간"),
                                                subsectionWithPath("clazzTimes[].endTime").description("수업 종료 시간")
                                        )
//                                .ignoredPaths("roundPay", "fixedDatePay") // 무시할 경로 지정
                                        .build()
                        )));
    }

    @Test
    @DisplayName("클래스 종료 여부 수정")
    void putIsDoneClazz() throws Exception {
        // given
        given(clazzService.updateClazz(anyLong(), anyBoolean())).willReturn(clazz);
        given(clazzMapper.clazzToClazzResponseCustom(clazz)).willReturn(response);

        // when, then
        mockMvc.perform(put("/class/{classId}", 1L)
                        .queryParam("isDone", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer access.token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("클래스 종료 여부 수정",
                        resource(ResourceSnippetParameters.builder()
                                .description("클래스를 종료 여부 수정")
                                .tag("Clazz API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .pathParameters(
                                        parameterWithName("classId").description("조회할 클래스 ID")
                                )
                                .queryParameters(
                                        parameterWithName("isDone").description("클래스 종료 여부 (true 또는 false)")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("수정할 클래스 ID"),
                                        fieldWithPath("title").description("클래스 이름"),
                                        fieldWithPath("subject").description("과목명"),
                                        fieldWithPath("isDone").description("클래스 종료 여부"),
                                        // responsePay 구조
                                        fieldWithPath("responsePay.id").description("지급 방식 ID"),
                                        fieldWithPath("responsePay.amount").description("급여 금액 (회당 금액 또는 월마다 지급 금액)"),
                                        fieldWithPath("responsePay.totalTime").description("총 수업 시간"),
                                        fieldWithPath("responsePay.currentRound").description("현재 회차"),
                                        // ResponseFixedDatePay 구조
                                        fieldWithPath("responsePay.date")
                                                .type(JsonFieldType.NUMBER)
                                                .optional()
                                                .description("급여 지급일 (정기적으로 설정된 경우, 회차 수당일 경우에는 null)"),
                                        // ResponseRoundPay 구조 (type 명시)
                                        fieldWithPath("responsePay.round")
                                                .type(JsonFieldType.NUMBER)
                                                .optional()
                                                .description("회차 수당 (회차 기반 지급일 경우, 급여 지급일일 경우에는 null)"),

                                        // ids 구조
                                        fieldWithPath("ids").description("관련 사람들 ID"),
                                        fieldWithPath("ids.teacherId").description("선생님 ID"),
                                        fieldWithPath("ids.parentId").description("부모님 ID"),
                                        fieldWithPath("ids.studentId").description("학생 ID"),

                                        // clazzTimes 구조
                                        fieldWithPath("clazzTimes").description("클래스의 수업 일정"),
                                        subsectionWithPath("clazzTimes[].id").description("수업 일정의 식별자").optional(),
                                        subsectionWithPath("clazzTimes[].clazzDate").description("수업 요일(MON, TUE, WED, THU, FRI, SAT, SUN)"),
                                        subsectionWithPath("clazzTimes[].startTime").description("수업 시작 시간"),
                                        subsectionWithPath("clazzTimes[].endTime").description("수업 종료 시간")
                                ).build()
                        )));
    }

    @Test
    @DisplayName("클래스 삭제")
    void deleteClazz() throws Exception {
        mockMvc.perform(delete("/class/{classId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer access.token"))
                .andExpect(status().isNoContent())
                .andDo(document("클래스 삭제",
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 클래스를 삭제")
                                .tag("Clazz API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .pathParameters(
                                        parameterWithName("classId").description("삭제할 클래스 ID")
                                ).build()
                        )));
    }
}