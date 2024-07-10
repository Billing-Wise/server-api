package site.billingwise.api.serverapi.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.servlet.http.Cookie;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.service.MemberService;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc
public class MemberControllerTest extends AbstractRestDocsTests {

    static final Long MEMBER_ID = 1L;

    @MockBean
    MemberService memberService;

    @Test
    @DisplayName("회원 생성")
    void createMember() throws Exception {
        // given
        String url = "/api/v1/members";

        CreateMemberDto createMemberDto = CreateMemberDto.builder()
                .name("kim")
                .email("example@example.com")
                .phone("010-1234-5678")
                .description("Test description")
                .build();

        willDoNothing().given(memberService).createMember(createMemberDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .content(objectMapper.writeValueAsString(createMemberDto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/create",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        requestFields(
                                fieldWithPath("name").description("회원명(* required)").type(JsonFieldType.STRING),
                                fieldWithPath("email").description("이메일(* required)").type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("전화번호(* required)").type(JsonFieldType.STRING),
                                fieldWithPath("description").description("상세설명").type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("회원 수정")
    void editMember() throws Exception {
        // given
        String url = "/api/v1/members/{memberId}";

        CreateMemberDto createMemberDto = CreateMemberDto.builder()
                .name("kim")
                .email("example@example.com")
                .phone("010-1234-5678")
                .description("Test description")
                .build();

        willDoNothing().given(memberService).editMember(anyLong(), eq(createMemberDto));

        // when
        ResultActions result = mockMvc.perform(put(url, MEMBER_ID)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .content(objectMapper.writeValueAsString(createMemberDto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/edit",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("memberId").description("회원 ID")),
                        requestFields(
                                fieldWithPath("name").description("회원명(* required)").type(JsonFieldType.STRING),
                                fieldWithPath("email").description("이메일(* required)").type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("전화번호(* required)").type(JsonFieldType.STRING),
                                fieldWithPath("description").description("상세설명").type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteMember() throws Exception {
        // given
        String url = "/api/v1/members/{memberId}";

        willDoNothing().given(memberService).deleteMember(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(url, MEMBER_ID)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/delete",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("memberId").description("회원 ID"))));
    }
}
