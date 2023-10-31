package com.pknu.studypro.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.auth.KakaoUser;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.dto.auth.RoleRequest;
import com.pknu.studypro.dto.auth.Tokens;
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
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
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
    @MockBean
    private AuthService authService;
    @MockBean
    private KakaoOAuthClient kakaoOAuthClient;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthArgumentResolver authArgumentResolver;

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
                .willReturn(KakaoUser.of("id", "nickname"));
        given(authService.login(any()))
                .willReturn(new Tokens("access.token.test", "refresh.token.test"));

        //when, then
        mockMvc.perform(post("/auth/login")
                        .queryParam("token", "kakao-oauth-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("로그인",
                        queryParameters(parameterWithName("token").description("카카오 토큰")),
                        responseFields(
                                fieldWithPath("access").description("액세스 토큰"),
                                fieldWithPath("refresh").description("리프레시 토큰"))));
    }

    @Test
    @DisplayName("토큰 리프레시")
    void refresh() throws Exception {
        //given
        given(authService.refresh(any()))
                .willReturn(new Tokens("access.token.test", "refresh.token.test"));
        final String tokens = objectMapper.writeValueAsString(
                new Tokens("access.token.test", "refresh.token.test"));

        //when, then
        mockMvc.perform(post("/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(tokens))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("토큰 갱신",
                        requestFields(
                                fieldWithPath("access").description("액세스 토큰"),
                                fieldWithPath("refresh").description("리프레시 토큰")),
                        responseFields(
                                fieldWithPath("access").description("액세스 토큰"),
                                fieldWithPath("refresh").description("리프레시 토큰"))));
    }

    @Test
    @DisplayName("유저 권한 변경")
    void changeRole() throws Exception {
        //given
        given(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(new LoginUser("id", Role.ANONYMOUS));
        final String role = objectMapper.writeValueAsString(new RoleRequest(Role.STUDENT));

        //when, then
        mockMvc.perform(post("/auth/role")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(APPLICATION_JSON)
                        .content(role))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("유저 권한 변경",
                        requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(fieldWithPath("role").description("변경할 권한"))));
    }
}
