package com.pknu.studypro.controller.auth;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.dto.auth.RoleRequest;
import com.pknu.studypro.dto.auth.Tokens;
import com.pknu.studypro.service.ClazzService;
import com.pknu.studypro.service.auth.AuthService;
import com.pknu.studypro.service.auth.KakaoOAuthClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.description;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;
    @MockBean
    private KakaoOAuthClient kakaoOAuthClient;
    @MockBean
    private KakaoTokenConverter kakaoTokenConverter;
    @MockBean
    private AuthArgumentResolver authArgumentResolver;
    @MockBean
    private ClazzService clazzService;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext,
               final RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {
        //given
        given(kakaoOAuthClient.findByKakaoToken(any()))
                .willReturn(KakaoUser.of("id", "nickname", "email", "phone_number", "profile_image"));
        given(authService.login(any()))
                .willReturn(new Tokens("Bearer service.access.token", "refresh.token.test"));

        //when, then
        mockMvc.perform(get("/auth/login")
                        .queryParam("token", "kakao-oauth-token"))
                .andDo(print())
                .andExpect(status().isOk());
//                .andDo(document("로그인",
//                        queryParameters(parameterWithName("token").description("카카오 토큰")),
//                        responseFields(
//                                fieldWithPath("access").description("액세스 토큰"),
//                                fieldWithPath("refresh").description("리프레시 토큰"))));
    }

    @Test
    @DisplayName("토큰 갱신")
    void refresh() throws Exception {
        //given
        given(authService.refresh(any()))
                .willReturn(new Tokens("Bearer service.access.token", "refresh.token.test"));
        final String tokens = objectMapper.writeValueAsString(
                new Tokens("Bearer service.access.token", "refresh.token.test"));

        //when, then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(tokens))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("토큰 갱신",
                        resource(ResourceSnippetParameters.builder()
                                .description("토큰 갱신")
                                .requestFields(
                                        fieldWithPath("access").description("액세스 토큰"),
                                        fieldWithPath("refresh").description("리프레시 토큰"))
                                .responseFields(
                                        fieldWithPath("access").description("액세스 토큰"),
                                        fieldWithPath("refresh").description("리프레시 토큰"))
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("유저 권한 변경")
    void changeRole() throws Exception {
        //given
        given(authArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(new LoginUser("id", Role.ANONYMOUS));
        final String role = objectMapper.writeValueAsString(new RoleRequest(Role.STUDENT));

        //when, then
        mockMvc.perform(post("/auth/role")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(APPLICATION_JSON)
                        .content(role))
                .andDo(print())
                .andExpect(status().isOk());
//                .andDo(document("유저 권한 변경",
//                        requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
//                        requestFields(fieldWithPath("role").description("변경할 권한"))
//                ));
    }

    @Test
    @DisplayName("유저 권한 조회")
    void getRole() throws Exception {
        //given
        given(authArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(new LoginUser("id", Role.STUDENT));

        //when, then
        mockMvc.perform(get("/auth/role")
                        .header(AUTHORIZATION, "Bearer service.access.token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("유저 권한 조회",
                        resource(ResourceSnippetParameters.builder()
                                .description("유저 권한 조회")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .responseFields(
                                        fieldWithPath("username").description("아이디"),
                                        fieldWithPath("role").description("현재 권한")
                                ).build()
                        )
                ));
    }
}
