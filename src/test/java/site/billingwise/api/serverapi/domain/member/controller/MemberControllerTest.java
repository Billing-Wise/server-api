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
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.servlet.http.Cookie;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.item.dto.response.GetItemDto;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.dto.response.GetMemberDto;
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
                                fieldWithPath("name").description("회원명(* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("email").description("이메일(* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("전화번호(* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("description").description("상세설명")
                                        .type(JsonFieldType.STRING))));
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
                                fieldWithPath("name").description("회원명(* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("email").description("이메일(* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("전화번호(* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("description").description("상세설명")
                                        .type(JsonFieldType.STRING))));
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

    @Test
    @DisplayName("회원 상세 조회")
    void getMember() throws Exception {
        // given
        String url = "/api/v1/members/{memberId}";

        GetMemberDto getMemberDto = GetMemberDto.builder()
                .id(1L)
                .name("Name")
                .email("test@example.com")
                .phone("123-456-7890")
                .description("Member Description")
                .contractCount(5L)
                .unPaidCount(2L)
                .totalInvoiceAmount(10000L)
                .totalUnpaidAmount(2000L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(memberService.getMember(anyLong())).willReturn(getMemberDto);

        // when
        ResultActions result = mockMvc.perform(get(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("member/get",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("memberId").description("회원ID")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.id").description("회원 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.name").description("회원명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.email").description("회원 이메일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.phone").description("회원 전화번호")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.description").description("회원 설명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.contractCount")
                                        .description("관련 계약수")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.unPaidCount").description("미납 계약수")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.totalInvoiceAmount")
                                        .description("총 청구 금액")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.totalUnpaidAmount")
                                        .description("총 미납 금액")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.createdAt").description("회원 생성일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.updatedAt").description("회원 정보 수정일")
                                        .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("회원 목록 조회")
    void getMemberList() throws Exception {
        // given
        String url = "/api/v1/members";

        GetMemberDto member1 = GetMemberDto.builder()
                .id(1L)
                .name("Member 1")
                .email("member1@example.com")
                .phone("010-1234-5678")
                .description("Member 1 Description")
                .contractCount(5L)
                .unPaidCount(2L)
                .totalInvoiceAmount(10000L)
                .totalUnpaidAmount(2000L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        GetMemberDto member2 = GetMemberDto.builder()
                .id(2L)
                .name("Member 2")
                .email("member2@example.com")
                .phone("010-8765-4321")
                .description("Member 2 Description")
                .contractCount(10L)
                .unPaidCount(1L)
                .totalInvoiceAmount(20000L)
                .totalUnpaidAmount(5000L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<GetMemberDto> memberList = Arrays.asList(member1, member2);

        given(memberService.getMemberList(anyString(), any(Pageable.class))).willReturn(memberList);

        // when
        ResultActions result = mockMvc.perform(get(url)
                .param("name", "Member")
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(document("member/get-list",
                requestCookies(
                        cookieWithName("access").description("엑세스 토큰")),
                pathParameters(
                        parameterWithName("name").optional().description("회원명"),
                        parameterWithName("page").optional().description("페이지 번호 (기본값: 0)"),
                        parameterWithName("size").optional().description("페이지 크기 (기본값: 20)")),
                responseFields(
                        fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                        fieldWithPath("message").description("응답 메시지")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data").description("응답 데이터").type(JsonFieldType.ARRAY),
                        fieldWithPath("data[].id").description("회원 ID")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].name").description("회원명")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data[].email").description("회원 이메일")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data[].phone").description("회원 전화번호")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data[].description").description("회원 설명")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data[].contractCount").description("관련 계약수")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].unPaidCount").description("미납된 계약수")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].totalInvoiceAmount").description("총 청구 금액")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].totalUnpaidAmount").description("총 미납 금액")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].createdAt").description("회원 생성일")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data[].updatedAt").description("회원 정보 수정일")
                                .type(JsonFieldType.STRING))));
    }
}
