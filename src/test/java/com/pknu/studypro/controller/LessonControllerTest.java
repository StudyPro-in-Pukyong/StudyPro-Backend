package com.pknu.studypro.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pknu.studypro.controller.auth.AuthArgumentResolver;
import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.domain.lesson.Type;
import com.pknu.studypro.dto.LessonRequestDto;
import com.pknu.studypro.dto.LessonResponseDto;
import com.pknu.studypro.mapper.LessonMapper;
import com.pknu.studypro.service.LessonService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LessonService lessonService;
    @MockBean
    private LessonMapper lessonMapper;
    @MockBean
    private AuthArgumentResolver authArgumentResolver;
    private Lesson lesson;
    private LessonResponseDto.Response response;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext,
               final RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        // 레슨 초기화
        lesson = new Lesson(1L, 1L, LocalDateTime.now(), 60, Type.ORIGINAL, false, "수업 진도");

        // 레슨 response 초기화
        response = new LessonResponseDto.Response();
        response.setId(1L);
        response.setClassId(1L);
        response.setStartTime(LocalDateTime.now());
        response.setMinutes(60);
        response.setIsDone(false);
        response.setType(Type.ORIGINAL);
        response.setProgress("수업 진도");
        response.getHomeworks().addAll(List.of("Homework1", "Homework2"));
    }

    @Test
    @DisplayName("수업 생성")
    void postLesson() throws Exception {
        // given
        LessonRequestDto.Post postDto = new LessonRequestDto.Post();
        postDto.setClassId(1L);
        postDto.setStartTime(LocalDateTime.now());
        postDto.setMinutes(60);
        postDto.setIsDone(false);
        postDto.setType(Type.ORIGINAL);
        postDto.setProgress("수업 진도");
        postDto.getHomeworks().addAll(List.of("Homework1", "Homework2"));

        given(lessonService.createLesson(any())).willReturn(lesson);
        given(lessonMapper.lessonToLessonResponseDto(lesson)).willReturn(response);

        // when, then
        mockMvc.perform(post("/lesson")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("수업 생성",
                        resource(ResourceSnippetParameters.builder()
                                .description("수업 생성")
                                .tag("Lesson API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .requestFields(
                                        fieldWithPath("classId").description("수업을 연결할 클래스 ID"),
                                        fieldWithPath("startTime").description("수업 시작 시간"),
                                        fieldWithPath("minutes").description("수업 시간 (분)"),
                                        fieldWithPath("isDone").description("수업 완료 여부"),
                                        fieldWithPath("type").description("수업 유형(ORIGINAL : 정규수업, MAKEUP : 보충수업)"),
                                        fieldWithPath("progress").description("수업 진도"),
                                        fieldWithPath("homeworks").description("숙제 목록")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("수업 ID"),
                                        fieldWithPath("classId").description("수업이 연결된 클래스 ID"),
                                        fieldWithPath("startTime").description("수업 시작 시간"),
                                        fieldWithPath("minutes").description("수업 시간 (분)"),
                                        fieldWithPath("isDone").description("수업 완료 여부"),
                                        fieldWithPath("type").description("수업 유형(ORIGINAL : 정규수업, MAKEUP : 보충수업)"),
                                        fieldWithPath("progress").description("수업 진도"),
                                        fieldWithPath("homeworks").description("숙제 목록")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("이번달 클래스 수업시간에 따른 레슨 생성")
    void postLessons() throws Exception {
        // given
        LessonRequestDto.Posts postDtos = new LessonRequestDto.Posts();
        postDtos.setClassId(1L);
        postDtos.setYear(2024);
        postDtos.setMonth(1);

        Lesson lesson2 = new Lesson(2L, 1L, LocalDateTime.now(), 60, Type.MAKEUP, false, "수업 진도");
        Lesson lesson3 = new Lesson(3L, 1L, LocalDateTime.now(), 60, Type.ORIGINAL, false, "수업 진도");
        List<Lesson> lessons = List.of(lesson, lesson2, lesson3);
        LessonResponseDto.Response response2 = response;
        response2.setId(2L);
        LessonResponseDto.Response response3 = response;
        response2.setId(3L);
        List<LessonResponseDto.Response> responses = List.of(response, response2, response3);

        given(lessonService.createLessons(anyLong(), anyInt(), anyInt())).willReturn(lessons);
        given(lessonMapper.lessonsToLessonResponseDtos(lessons)).willReturn(responses);

        // when, then
        mockMvc.perform(post("/lessons")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDtos)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("이번달 클래스 수업시간에 따른 레슨 생성",
                        resource(ResourceSnippetParameters.builder()
                                .description("이번달 클래스 수업시간에 따른 레슨 생성")
                                .tag("Lesson API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .requestFields(
                                        fieldWithPath("classId").description("수업을 연결할 클래스 ID"),
                                        fieldWithPath("year").description("수업을 생성할 년도"),
                                        fieldWithPath("month").description("수업을 생성할 월")
                                )
                                .responseFields(
                                        fieldWithPath("[].id").description("수업 ID"),
                                        fieldWithPath("[].classId").description("수업이 연결된 클래스 ID"),
                                        fieldWithPath("[].startTime").description("수업 시작 시간"),
                                        fieldWithPath("[].minutes").description("수업 시간 (분)"),
                                        fieldWithPath("[].isDone").description("수업 완료 여부"),
                                        fieldWithPath("[].type").description("수업 유형(ORIGINAL : 정규수업, MAKEUP : 보충수업)"),
                                        fieldWithPath("[].progress").description("수업 진도"),
                                        fieldWithPath("[].homeworks").description("숙제 목록")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("수업 조회")
    void getLesson() throws Exception {
        // given
        given(lessonService.readLesson(anyLong())).willReturn(lesson);
        given(lessonMapper.lessonToLessonResponseDto(lesson)).willReturn(response);

        // when, then
        mockMvc.perform(get("/lesson/{lessonId}", 1L)
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("수업 조회",
                        resource(ResourceSnippetParameters.builder()
                                .description("수업 조회")
                                .tag("Lesson API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .pathParameters(
                                        parameterWithName("lessonId").description("조회할 수업의 ID")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("수업 ID"),
                                        fieldWithPath("classId").description("수업이 연결된 클래스 ID"),
                                        fieldWithPath("startTime").description("수업 시작 시간"),
                                        fieldWithPath("minutes").description("수업 시간 (분)"),
                                        fieldWithPath("isDone").description("수업 완료 여부"),
                                        fieldWithPath("type").description("수업 유형(ORIGINAL : 정규수업, MAKEUP : 보충수업)"),
                                        fieldWithPath("progress").description("수업 진도"),
                                        fieldWithPath("homeworks").description("숙제 목록")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("레슨 목록 조회")
    void getLessons() throws Exception {
        // given
        Lesson lesson2 = new Lesson(2L, 1L, LocalDateTime.now(), 60, Type.MAKEUP, false, "수업 진도");
        Lesson lesson3 = new Lesson(3L, 1L, LocalDateTime.now(), 60, Type.ORIGINAL, false, "수업 진도");
        List<Lesson> lessons = List.of(lesson, lesson2, lesson3);
        LessonResponseDto.Response response2 = response;
        response2.setId(2L);
        LessonResponseDto.Response response3 = response;
        response2.setId(3L);
        List<LessonResponseDto.Response> responses = List.of(response, response2, response3);

        given(lessonService.readLessons(any(LocalDate.class), anyLong())).willReturn(lessons);
        given(lessonMapper.lessonsToLessonResponseDtos(lessons)).willReturn(responses);

        // when, then
        mockMvc.perform(get("/lessons")
                        .param("year", "2024")
                        .param("month", "11")
                        .param("classId", "1")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("레슨 목록 조회",
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 연도, 월, 클래스 ID에 해당하는 레슨 목록 조회")
                                .tag("Lesson API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .queryParameters(
                                        parameterWithName("year").description("조회할 연도"),
                                        parameterWithName("month").description("조회할 월"),
                                        parameterWithName("classId").description("조회할 클래스 ID")
                                )
                                .responseFields(
                                        fieldWithPath("[].id").description("수업 ID"),
                                        fieldWithPath("[].classId").description("수업이 연결된 클래스 ID"),
                                        fieldWithPath("[].startTime").description("수업 시작 시간"),
                                        fieldWithPath("[].minutes").description("수업 시간 (분)"),
                                        fieldWithPath("[].isDone").description("수업 완료 여부"),
                                        fieldWithPath("[].type").description("수업 유형(ORIGINAL : 정규수업, MAKEUP : 보충수업)"),
                                        fieldWithPath("[].progress").description("수업 진도"),
                                        fieldWithPath("[].homeworks").description("숙제 목록")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("레슨 업데이트")
    void putLesson() throws Exception {
        // given
        LessonRequestDto.Put putDto = new LessonRequestDto.Put();
        putDto.setId(1L);
        putDto.setClassId(1L);
        putDto.setStartTime(LocalDateTime.now());
        putDto.setMinutes(90);
        putDto.setIsDone(false);
        putDto.setType(Type.MAKEUP);
        putDto.setProgress("수정된 수업 진도");
        putDto.getHomeworks().addAll(List.of("Homework 1", "Homework 2"));

        given(lessonMapper.lessonPutDtoToLesson(any(LessonRequestDto.Put.class))).willReturn(lesson);
        given(lessonService.updateLesson(any(Lesson.class))).willReturn(lesson);
        given(lessonMapper.lessonToLessonResponseDto(any(Lesson.class))).willReturn(response);

        // when, then
        mockMvc.perform(put("/lesson")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .content(objectMapper.writeValueAsString(putDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("레슨 업데이트",
                        resource(ResourceSnippetParameters.builder()
                                .description("레슨 업데이트")
                                .tag("Lesson API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .requestFields(
                                        fieldWithPath("id").description("레슨 ID"),
                                        fieldWithPath("classId").description("연관된 클래스 ID"),
                                        fieldWithPath("startTime").description("수업 시작 시간"),
                                        fieldWithPath("minutes").description("수업 시간 (분)"),
                                        fieldWithPath("isDone").description("수업 완료 여부"),
                                        fieldWithPath("type").description("수업 유형(ORIGINAL : 정규수업, MAKEUP : 보충수업)"),
                                        fieldWithPath("progress").description("수업 진도"),
                                        fieldWithPath("homeworks").description("숙제 목록")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("레슨 ID"),
                                        fieldWithPath("classId").description("연관된 클래스 ID"),
                                        fieldWithPath("startTime").description("수업 시작 시간"),
                                        fieldWithPath("minutes").description("수업 시간 (분)"),
                                        fieldWithPath("isDone").description("수업 완료 여부"),
                                        fieldWithPath("type").description("수업 유형(ORIGINAL : 정규수업, MAKEUP : 보충수업)"),
                                        fieldWithPath("progress").description("수업 진도"),
                                        fieldWithPath("homeworks").description("숙제 목록")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("레슨 완료 여부 업데이트")
    void putIsDoneLesson() throws Exception {
        // given
        response.setIsDone(true);
        given(lessonService.updateIsDoneLesson(anyLong(), anyBoolean())).willReturn(lesson);
        given(lessonMapper.lessonToLessonResponseDto(lesson)).willReturn(response);

        // when, then
        mockMvc.perform(put("/lesson/{lessonId}", 1L)
                        .queryParam("isDone", "true")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("레슨 완료 여부 업데이트",
                        resource(ResourceSnippetParameters.builder()
                                .description("레슨의 완료 상태를 업데이트")
                                .tag("Lesson API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .pathParameters(
                                        parameterWithName("lessonId").description("수정할 레슨 ID")
                                )
                                .queryParameters(
                                        parameterWithName("isDone").description("수업 완료 여부 (true 또는 false)")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("레슨 ID"),
                                        fieldWithPath("classId").description("연관된 클래스 ID"),
                                        fieldWithPath("startTime").description("수업 시작 시간"),
                                        fieldWithPath("minutes").description("수업 시간 (분)"),
                                        fieldWithPath("isDone").description("수업 완료 여부"),
                                        fieldWithPath("type").description("수업 유형(ORIGINAL : 정규수업, MAKEUP : 보충수업)"),
                                        fieldWithPath("progress").description("수업 진도"),
                                        fieldWithPath("homeworks").description("숙제 목록")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("수업 삭제")
    void deleteLesson() throws Exception {
        // when, then
        mockMvc.perform(delete("/lesson/{lessonId}", 1L)
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("수업 삭제",
                        resource(ResourceSnippetParameters.builder()
                                .description("수업 삭제")
                                .tag("Lesson API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .pathParameters(
                                        parameterWithName("lessonId").description("삭제할 수업의 ID")
                                )
                                .build()
                        )
                ));
    }
}