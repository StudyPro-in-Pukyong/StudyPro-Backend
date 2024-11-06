package com.pknu.studypro.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pknu.studypro.controller.auth.AuthArgumentResolver;
import com.pknu.studypro.controller.auth.KakaoTokenConverter;
import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.MemberRequestDto;
import com.pknu.studypro.dto.MemberResponseDto;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.mapper.MemberMapper;
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

import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberMapper memberMapper;
    @MockBean
    private MemberService memberService;
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
    @DisplayName("토큰을 이용해서 회원 정보 반환")
    void findMember() throws Exception {
        // given
        long memberId = 1L;
        String nickname = "nickname";
        Member member = new Member(Role.PARENT, null, "username", null, nickname, null, null);
        MemberResponseDto.Response response = new MemberResponseDto.Response(memberId, nickname);
        given(memberService.verifiedMember(any(LoginUser.class))).willReturn(member);
        given(memberMapper.memberToMemberResponseDto(member)).willReturn(response);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/member")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andDo(document("토큰을 이용해서 회원 정보 반환",
                        resource(ResourceSnippetParameters.builder()
                                .description("토큰을 이용해서 회원 정보 반환")
                                .tag("Member API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .responseFields(
                                        fieldWithPath("id").description("회원의 고유 ID"),
                                        fieldWithPath("nickname").description("회원의 닉네임")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("해당 닉네임과 역할에 대한 회원들 검색")
    void findMembers() throws Exception {
        // given
        long[] memberIds = {1L, 2L, 3L};
        String nickname = "nickname";
        Member member1 = new Member(Role.PARENT, LoginType.KAKAO, "username", null, nickname, "email", "phoneNumber");
        Member member2 = new Member(Role.PARENT, LoginType.KAKAO, "username", null, nickname, "email", "phoneNumber");
        Member member3 = new Member(Role.PARENT, LoginType.KAKAO, "username", null, nickname, "email", "phoneNumber");
        List<Member> members = List.of(member1, member2, member3);
        List<MemberResponseDto.Response> responses = List.of(new MemberResponseDto.Response(memberIds[0], nickname),
                new MemberResponseDto.Response(memberIds[1], nickname),
                new MemberResponseDto.Response(memberIds[2], nickname));
        given(memberService.verifiedMembers(any(), anyString(), any(Role.class))).willReturn(members);
        given(memberMapper.membersToMemberResponseDtos(members)).willReturn(responses);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/members")
                        .param("nickname", "nickname")
                        .param("role", Role.PARENT.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nickname").value(nickname))
                .andExpect(jsonPath("$[1].nickname").value(nickname))
                .andExpect(jsonPath("$[2].nickname").value(nickname))
                .andDo(document("해당 닉네임과 역할에 대한 회원들 검색",
                        resource(ResourceSnippetParameters.builder()
                                .description("해당 닉네임과 역할에 대한 회원들 검색")
                                .tag("Member API")
                                .queryParameters(
                                        parameterWithName("nickname").description("조회할 회원의 닉네임"),
                                        parameterWithName("role").description("회원의 역할")
                                )
                                .responseFields(
                                        subsectionWithPath("[].id").description("회원의 고유 ID"),
                                        subsectionWithPath("[].nickname").description("조회한 회원의 닉네임")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("회원 정보 업데이트")
    void updateMember() throws Exception {
        // given
        long memberId = 1L;
        String nickname = "updatedNickname";
        Member member = new Member(Role.PARENT, null, "username", null, nickname, null, null);
        MemberRequestDto.Put memberRequest = new MemberRequestDto.Put(memberId, nickname);
        MemberResponseDto.Response response = new MemberResponseDto.Response(memberId, nickname);
        given(memberService.updateMember(any(LoginUser.class), anyString())).willReturn(member);
        given(memberMapper.memberToMemberResponseDto(member)).willReturn(response);

        // when, then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/member")
                        .header(AUTHORIZATION, "Bearer service.access.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("updatedNickname"))
                .andDo(document("회원 정보 업데이트",
                        resource(ResourceSnippetParameters.builder()
                                .description("회원 정보 업데이트")
                                .tag("Member API")
                                .requestHeaders(headerWithName(AUTHORIZATION).description("액세스 토큰"))
                                .requestFields(
                                        fieldWithPath("id").description("회원의 고유 ID"),
                                        fieldWithPath("nickname").description("변경할 닉네임")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("회원의 고유 ID"),
                                        fieldWithPath("nickname").description("변경된 회원의 닉네임")
                                ).build()
                        )
                ));
    }
}
