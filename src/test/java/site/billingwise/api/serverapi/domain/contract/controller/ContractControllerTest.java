package site.billingwise.api.serverapi.domain.contract.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

import org.junit.jupiter.api.BeforeEach;
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

import jakarta.servlet.http.Cookie;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.contract.dto.request.CreateContractDto;
import site.billingwise.api.serverapi.domain.contract.dto.request.EditContractDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.ContractInvoiceTypeDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.ContractItemDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.ContractMemberDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.ContractStatusDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.CreateBulkContractResultDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.GetContractAllDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.GetContractDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.PaymentTypeDto;
import site.billingwise.api.serverapi.domain.contract.service.ContractService;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;

@WebMvcTest(ContractController.class)
@AutoConfigureMockMvc
public class ContractControllerTest extends AbstractRestDocsTests {

    @MockBean
    ContractService contractService;

    private ContractItemDto itemDto;
    private ContractMemberDto memberDto;
    private PaymentTypeDto paymentTypeDto;
    private ContractInvoiceTypeDto invoiceTypeDto;
    private ContractStatusDto contractStatusDto;

    @BeforeEach
    void setUp() {
        itemDto = ContractItemDto.builder()
                .id(1L)
                .name("Item")
                .price(1000L)
                .amount(10)
                .build();

        memberDto = ContractMemberDto.builder()
                .id(1L)
                .name("member")
                .email("example@example.com")
                .phone("010-1234-5678")
                .build();

        paymentTypeDto = PaymentTypeDto.builder()
                .id(PaymentType.AUTO_TRANSFER.getId())
                .name(PaymentType.AUTO_TRANSFER.getName())
                .build();

        invoiceTypeDto = ContractInvoiceTypeDto.builder()
                .id(InvoiceType.AUTO.getId())
                .name(InvoiceType.AUTO.getName())
                .build();

        contractStatusDto = ContractStatusDto.builder()
                .id(ContractStatus.PROGRESS.getId())
                .name(ContractStatus.PROGRESS.getName())
                .build();

    }

    @Test
    @DisplayName("계약 생성")
    void createContract() throws Exception {
        // given
        String url = "/api/v1/contracts";

        CreateContractDto createContractDto = CreateContractDto.builder()
                .memberId(1L)
                .itemId(1L)
                .itemPrice(10000L)
                .itemAmount(5)
                .isSubscription(true)
                .invoiceTypeId(1L)
                .paymentTypeId(2L)
                .isEasyConsent(true)
                .contractCycle(10)
                .paymentDueCycle(15)
                .build();

        willDoNothing().given(contractService).createContract(createContractDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .content(objectMapper.writeValueAsString(createContractDto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("contract/create",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        requestFields(
                                fieldWithPath("memberId")
                                        .description("회원 정보(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("itemId").description("상품 정보(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("itemPrice")
                                        .description("금액 정보(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("itemAmount")
                                        .description("상품 수량(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("isSubscription")
                                        .description("구독 여부(* required)")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("invoiceTypeId")
                                        .description("청구 타입(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("paymentTypeId")
                                        .description("결제 수단(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("isEasyConsent")
                                        .description("간편 동의 여부(* required)")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("contractCycle")
                                        .description("약정일(* required, 1~30일)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("paymentDueCycle")
                                        .description("납부 기한(* required, 1~30일)")
                                        .type(JsonFieldType.NUMBER))));
    }

    @Test
    @DisplayName("계약 수정")
    void editContract() throws Exception {
        // given
        String url = "/api/v1/contracts/{contractId}";

        EditContractDto editContractDto = EditContractDto.builder()
                .itemPrice(10000L)
                .itemAmount(5)
                .invoiceTypeId(1L)
                .paymentTypeId(2L)
                .isEasyConsent(true)
                .contractCycle(10)
                .paymentDueCycle(15)
                .build();

        willDoNothing().given(contractService).editContract(anyLong(), eq(editContractDto));

        // when
        ResultActions result = mockMvc.perform(put(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .content(objectMapper.writeValueAsString(editContractDto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("contract/edit",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("contractId").description("계약 ID")),
                        requestFields(
                                fieldWithPath("itemPrice")
                                        .description("금액 정보(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("itemAmount")
                                        .description("상품 수량(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("invoiceTypeId")
                                        .description("청구 타입(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("paymentTypeId")
                                        .description("결제 수단(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("isEasyConsent")
                                        .description("간편 동의 여부(* required)")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("contractCycle")
                                        .description("약정일(* required, 1~30일)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("paymentDueCycle")
                                        .description("납부 기한(* required, 1~30일)")
                                        .type(JsonFieldType.NUMBER))));
    }

    @Test
    @DisplayName("계약 삭제")
    void deleteContract() throws Exception {
        // given
        String url = "/api/v1/contracts/{contractId}";

        willDoNothing().given(contractService).deleteContract(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("contract/delete",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("contractId").description("계약 ID"))));
    }

    @Test
    @DisplayName("계약 상세 조회")
    void getContract() throws Exception {
        // given
        String url = "/api/v1/contracts/{contractId}";

        GetContractDto getContractDto = GetContractDto.builder()
                .id(1L)
                .member(memberDto)
                .item(itemDto)
                .chargeAmount(10000L)
                .isSubscription(true)
                .isEasyConsent(true)
                .totalChargeAmount(100000L)
                .totalUnpaidAmount(50000L)
                .invoiceType(invoiceTypeDto)
                .paymentType(paymentTypeDto)
                .contractCycle(30)
                .paymentDueCycle(15)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(contractService.getContract(anyLong())).willReturn(getContractDto);

        // when
        ResultActions result = mockMvc.perform(get(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("contract/get",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("contractId").description("계약 ID")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.id").description("계약 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.member").description("회원 정보")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.member.id").description("회원 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.member.name").description("회원명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.member.phone")
                                        .description("회원 전화번호")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.member.email").description("회원 이메일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.item").description("상품 정보")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.item.id").description("상품 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.item.name").description("상품명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.item.price").description("상품 가격")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.item.amount").description("상품 수량")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.chargeAmount").description("금액 정보")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.chargeAmount").description("금액 정보")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.subscription").description("구독 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.easyConsent")
                                        .description("간편 동의 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.totalChargeAmount")
                                        .description("총 청구 금액")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.totalUnpaidAmount")
                                        .description("총 미납 금액")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.invoiceType").description("청구 타입")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.invoiceType.id")
                                        .description("청구 타입 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.invoiceType.name")
                                        .description("청구 타입명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.paymentType").description("결제 수단")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.paymentType.id")
                                        .description("결제 수단 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.paymentType.name")
                                        .description("결제 수단명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.contractCycle").description("약정일")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.paymentDueCycle")
                                        .description("납부 기한")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.createdAt").description("계약 생성일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.updatedAt").description("계약 정보 수정일")
                                        .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("계약 목록 조회")
    void getContractList() throws Exception {
        // given
        String url = "/api/v1/contracts";

        GetContractAllDto getContractDto1 = GetContractAllDto.builder()
                .id(1L)
                .memberName("member1")
                .itemName("item1")
                .chargeAmount(10000L)
                .isSubscription(true)
                .totalUnpaidCount(3L)
                .invoiceType(invoiceTypeDto)
                .paymentType(paymentTypeDto)
                .contractStatus(contractStatusDto)
                .contractCycle(30)
                .paymentDueCycle(15)
                .build();

        GetContractAllDto getContractDto2 = GetContractAllDto.builder()
                .id(1L)
                .memberName("member2")
                .itemName("item2")
                .chargeAmount(20000L)
                .isSubscription(true)
                .totalUnpaidCount(5L)
                .invoiceType(invoiceTypeDto)
                .paymentType(paymentTypeDto)
                .contractStatus(contractStatusDto)
                .contractCycle(30)
                .paymentDueCycle(15)
                .build();

        List<GetContractAllDto> contractList = Arrays.asList(getContractDto1, getContractDto2);

        PageImpl<GetContractAllDto> page = new PageImpl<>(contractList, PageRequest.of(0, 20),
                contractList.size());
        given(contractService.getContractList(anyLong(), anyLong(), anyString(), anyString(), anyBoolean(),
                anyLong(), anyLong(),
                anyLong(),
                any(Pageable.class))).willReturn(page);

        // when
        ResultActions result = mockMvc.perform(get(url)
                .param("itemId", "1")
                .param("memberId", "2")
                .param("itemName", "item")
                .param("memberName", "member")
                .param("isSubscription", "true")
                .param("invoiceTypeId", "1")
                .param("contractStatusId", "1")
                .param("paymentTypeId", "2")
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andDo(document("contract/get-list",
                requestCookies(
                        cookieWithName("access").description("엑세스 토큰")),
                pathParameters(
                        parameterWithName("itemId").optional().description("상품Id"),
                        parameterWithName("memberId").optional().description("회원Id"),
                        parameterWithName("itemName").optional().description("상품명"),
                        parameterWithName("memberName").optional().description("회원명"),
                        parameterWithName("isSubscription").optional().description("구독 여부"),
                        parameterWithName("invoiceTypeId").optional().description("청구 타입 ID"),
                        parameterWithName("contractStatusId").optional()
                                .description("계약 상태 ID"),
                        parameterWithName("paymentTypeId").optional().description("결제 수단 ID"),
                        parameterWithName("page").optional().description("페이지 번호 (기본값: 0)"),
                        parameterWithName("size").optional().description("페이지 크기 (기본값: 20)")),
                responseFields(
                        fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                        fieldWithPath("message").description("응답 메시지")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data").description("응답 데이터").type(JsonFieldType.OBJECT),
                        fieldWithPath("data.content").description("페이지 계약 목록")
                                .type(JsonFieldType.ARRAY),
                        fieldWithPath("data.content[].id").description("계약 ID")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].memberName").description("회원명")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].itemName").description("상품명")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].chargeAmount").description("청구 금액")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].subscription").description("구독 여부")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data.content[].totalUnpaidCount").description("총 미납 횟수")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].invoiceType").description("청구 타입 정보")
                                .type(JsonFieldType.OBJECT),
                        fieldWithPath("data.content[].invoiceType.id").description("청구 타입 ID")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].invoiceType.name").description("청구 타입명")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].paymentType").description("결제 수단 정보")
                                .type(JsonFieldType.OBJECT),
                        fieldWithPath("data.content[].paymentType.id").description("결제 수단 ID")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].paymentType.name").description("결제 수단명")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].contractStatus").description("결제 상태")
                                .type(JsonFieldType.OBJECT),
                        fieldWithPath("data.content[].contractStatus.id")
                                .description("결제 상태 ID")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].contractStatus.name")
                                .description("결제 상태명")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data.content[].contractCycle").description("계약 주기")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data.content[].paymentDueCycle").description("납부 기한")
                                .type(JsonFieldType.NUMBER),
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
    @DisplayName("계약 대량 등록 - 성공")
    public void createContractBulkSuccess() throws Exception {
        // given
        String url = "/api/v1/contracts/bulk-register";

        MockMultipartFile file = new MockMultipartFile("file", "member_test_success.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "exel data".getBytes());

        CreateContractDto createContractDto = CreateContractDto.builder()
                .memberId(1L)
                .itemId(1L)
                .itemPrice(10000L)
                .itemAmount(5)
                .isSubscription(true)
                .invoiceTypeId(1L)
                .paymentTypeId(2L)
                .isEasyConsent(true)
                .contractCycle(10)
                .paymentDueCycle(15)
                .build();

        List<CreateContractDto> contractList = new ArrayList<>();
        contractList.add(createContractDto);

        List<String> errorList = new ArrayList<>();

        CreateBulkContractResultDto createBulkContractResultDto = CreateBulkContractResultDto.builder()
                .isSuccess(true)
                .contractList(contractList)
                .errorList(errorList)
                .build();

        given(contractService.createContractBulk(file)).willReturn(createBulkContractResultDto);

        // when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // given
        result.andDo(document("contract/bulk-register/success",
                requestCookies(
                        cookieWithName("access").description("엑세스 토큰")),
                requestParts(
                        partWithName("file").description("업로드할 엑셀 파일")),
                responseFields(
                        fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                        fieldWithPath("message").description("응답 메시지")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data").description("등록된 계약 목록")
                                .type(JsonFieldType.ARRAY),
                        fieldWithPath("data[].memberId")
                                .description("회원 정보(* required)")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].itemId").description("상품 정보(* required)")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].itemPrice")
                                .description("금액 정보(* required)")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].itemAmount")
                                .description("상품 수량(* required)")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].isSubscription")
                                .description("구독 여부(* required)")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data[].invoiceTypeId")
                                .description("청구 타입(* required)")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].paymentTypeId")
                                .description("결제 수단(* required)")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].isEasyConsent")
                                .description("간편 동의 여부(* required)")
                                .type(JsonFieldType.BOOLEAN),
                        fieldWithPath("data[].contractCycle")
                                .description("약정일(* required, 1~30일)")
                                .type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].paymentDueCycle")
                                .description("납부 기한(* required, 1~30일)")
                                .type(JsonFieldType.NUMBER))));
    }

    @Test
    @DisplayName("계약 대량 등록 - 실패")
    public void createContractBulkFail() throws Exception {
        // given
        String url = "/api/v1/contracts/bulk-register";

        MockMultipartFile file = new MockMultipartFile("file", "member_test_success.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "exel data".getBytes());

        CreateContractDto createContractDto = CreateContractDto.builder()
                .memberId(null)
                .itemId(1L)
                .itemPrice(10000L)
                .itemAmount(5)
                .isSubscription(true)
                .invoiceTypeId(1L)
                .paymentTypeId(2L)
                .isEasyConsent(true)
                .contractCycle(10)
                .paymentDueCycle(15)
                .build();

        List<CreateContractDto> contractList = new ArrayList<>();
        contractList.add(createContractDto);

        List<String> errorList = new ArrayList<>();
        errorList.add("1행: 회원 정보는 필수 입력값입니다.");

        CreateBulkContractResultDto createBulkContractResultDto = CreateBulkContractResultDto.builder()
                .isSuccess(false)
                .contractList(contractList)
                .errorList(errorList)
                .build();

        given(contractService.createContractBulk(file)).willReturn(createBulkContractResultDto);

        // when
        ResultActions result = mockMvc.perform(multipart(url)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // given
        result.andDo(document("contract/bulk-register/fail",
                requestCookies(
                        cookieWithName("access").description("엑세스 토큰")),
                requestParts(
                        partWithName("file").description("업로드할 엑셀 파일")),
                responseFields(
                        fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                        fieldWithPath("message").description("응답 메시지")
                                .type(JsonFieldType.STRING),
                        fieldWithPath("data").description("실패 메시지 배열")
                                .type(JsonFieldType.ARRAY))));
    }

}
