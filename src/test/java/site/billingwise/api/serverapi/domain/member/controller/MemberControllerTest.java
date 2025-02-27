package site.billingwise.api.serverapi.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.member.dto.request.CreateMemberDto;
import site.billingwise.api.serverapi.domain.member.dto.response.CreateBulkResultDto;
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
                .phone("01012345678")
                .description("Test description")
                .build();

        given(memberService.createMember(any(CreateMemberDto.class))).willReturn(3L);

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
                                        .type(JsonFieldType.STRING)),
                        responseFields(
                                fieldWithPath("code").description("응답 코드")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data").description("회원 번호")
                                        .type(JsonFieldType.NUMBER))));
    }

    @Test
    @DisplayName("회원 수정")
    void editMember() throws Exception {
        // given
        String url = "/api/v1/members/{memberId}";

        CreateMemberDto createMemberDto = CreateMemberDto.builder()
                .name("kim")
                .email("example@example.com")
                .phone("01012345678")
                .description("Test description")
                .build();

        GetMemberDto getMemberDto = GetMemberDto.builder()
                .id(1L)
                .name("kim")
                .email("example@example.com")
                .phone("01012345678")
                .description("Member Description")
                .contractCount(5L)
                .unPaidCount(2L)
                .totalInvoiceAmount(10000L)
                .totalUnpaidAmount(2000L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(memberService.editMember(anyLong(), any(CreateMemberDto.class))).willReturn(getMemberDto);

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
                                        .type(JsonFieldType.STRING)),
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
                .phone("1234567890")
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

        PageImpl<GetMemberDto> page = new PageImpl<>(memberList, PageRequest.of(0, 20), memberList.size());
        given(memberService.getMemberList(nullable(String.class), nullable(String.class),
                nullable(String.class), any(Pageable.class))).willReturn(page);

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
                        parameterWithName("email").optional().description("이메일"),
                        parameterWithName("phone").optional().description("전화번호"),
                        parameterWithName("page").optional().description("페이지 번호 (기본값: 0)"),
                        parameterWithName("size").optional().description("페이지 크기 (기본값: 20)")),
                responseFields(
                        fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                        fieldWithPath("message").description("응답 메시지")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data").description("응답 데이터").type(JsonFieldType.OBJECT),
                        fieldWithPath("data.content").description("회원 목록")
                                .type(JsonFieldType.ARRAY),
                        fieldWithPath("data.content[].id").description("회원 ID")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].name").description("회원명")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].email").description("회원 이메일")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].phone").description("회원 전화번호")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].description").description("회원 설명")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].contractCount").description("관련 계약수")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].unPaidCount").description("미납된 계약수")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].totalInvoiceAmount")
                                .description("총 청구 금액")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].totalUnpaidAmount").description("총 미납 금액")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].createdAt").description("회원 생성일")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].updatedAt").description("회원 정보 수정일")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.pageable").description("페이징 정보")
                                .type(JsonFieldType.OBJECT),
                        fieldWithPath("data.pageable.sort").description("정렬 정보")
                                .type(JsonFieldType.OBJECT),
                        fieldWithPath("data.pageable.sort.empty").description("정렬 정보 비어 있음 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.pageable.sort.sorted").description("정렬 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않음 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.pageable.offset").description("페이징 오프셋")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.pageable.pageNumber").description("페이지 번호")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.pageable.pageSize").description("페이지 크기")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.pageable.paged").description("페이징 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.pageable.unpaged").description("페이징되지 않음 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.last").description("마지막 페이지 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.totalPages").description("전체 페이지 수")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.totalElements").description("전체 요소 수")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.size").description("페이지 크기")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.number").description("현재 페이지 번호")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.sort").description("정렬 정보")
                                .type(JsonFieldType.OBJECT),
                        fieldWithPath("data.sort.empty").description("정렬 정보 비어 있음 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.sort.sorted").description("정렬 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.sort.unsorted").description("정렬되지 않음 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.first").description("첫 페이지 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.numberOfElements").description("요소 개수")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.empty").description("비어 있음 여부")
                                .type(JsonFieldType.BOOLEAN))));
    }

    @Test
    @DisplayName("회원 대량 등록 - 성공")
    public void createMemberBulkSuccess() throws Exception {
        // given
        String url = "/api/v1/members/bulk-register";

        MockMultipartFile file = new MockMultipartFile("file", "member_test_success.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "exel data".getBytes());

        CreateMemberDto createMemberDto = CreateMemberDto.builder()
                .name("kim")
                .email("example@example.com")
                .phone("010-1234-5678")
                .description("Test description")
                .build();

        List<CreateMemberDto> memberList = new ArrayList<>();
        memberList.add(createMemberDto);

        CreateBulkResultDto createBulkResultDto = CreateBulkResultDto.builder()
                .isSuccess(true)
                .memberList(memberList)
                .errorList(new ArrayList<>())
                .build();

        given(memberService.createMemberBulk(any(MultipartFile.class))).willReturn(createBulkResultDto);

        // when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // given
        result.andDo(document("member/bulk-register/success",
                requestCookies(
                        cookieWithName("access").description("엑세스 토큰")),
                requestParts(
                        partWithName("file").description("업로드할 엑셀 파일")),
                responseFields(
                        fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                        fieldWithPath("message").description("응답 메시지")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data").description("등록된 회원 목록")
                                .type(JsonFieldType.ARRAY),
                        fieldWithPath("data[].name").description("회원 이름")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data[].email").description("회원 이메일")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data[].phone").description("회원 전화번호")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data[].description").description("회원 설명")
                                .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("회원 대량 등록 - 실패")
    public void createMemberBulkFail() throws Exception {
        // given
        String url = "/api/v1/members/bulk-register";

        MockMultipartFile file = new MockMultipartFile("file", "member_test_success.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "exel data".getBytes());

        List<String> errorList = new ArrayList<>();
        errorList.add("1행 : 중복된 이메일입니다.");

        CreateBulkResultDto createBulkResultDto = CreateBulkResultDto.builder()
                .isSuccess(false)
                .memberList(new ArrayList<>())
                .errorList(errorList)
                .build();

        given(memberService.createMemberBulk(any(MultipartFile.class))).willReturn(createBulkResultDto);

        // when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // given
        result.andDo(document("member/bulk-register/fail",
                requestCookies(
                        cookieWithName("access").description("엑세스 토큰")),
                requestParts(
                        partWithName("file").description("업로드할 엑셀 파일")),
                responseFields(
                        fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                        fieldWithPath("message").description("응답 메시지")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data").description("오류 목록")
                                .type(JsonFieldType.ARRAY).optional())));
    }
}
