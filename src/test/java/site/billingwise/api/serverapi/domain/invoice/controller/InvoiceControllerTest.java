package site.billingwise.api.serverapi.domain.invoice.controller;

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
import java.time.LocalDate;

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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.servlet.http.Cookie;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.contract.dto.response.PaymentTypeDto;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;
import site.billingwise.api.serverapi.domain.invoice.dto.request.CreateInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.request.EditInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.GetInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.GetInvoiceListDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.InvoiceItemDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.InvoiceMemberDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.InvoiceTypeDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.PaymentStatusDto;
import site.billingwise.api.serverapi.domain.invoice.service.InvoiceService;

@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc
public class InvoiceControllerTest extends AbstractRestDocsTests {

    @MockBean
    InvoiceService invoiceService;

    private InvoiceItemDto itemDto;
    private InvoiceMemberDto memberDto;
    private PaymentTypeDto paymentTypeDto;
    private InvoiceTypeDto invoiceTypeDto;
    private PaymentStatusDto paymentStatusDto;

    @BeforeEach
    void setUp() {
        itemDto = InvoiceItemDto.builder()
                .itemId(1L)
                .name("Item")
                .price(1000L)
                .amount(10)
                .build();

        memberDto = InvoiceMemberDto.builder()
                .memberId(1L)
                .name("member")
                .email("example@example.com")
                .phone("010-1234-5678")
                .build();

        paymentTypeDto = PaymentTypeDto.builder()
                .id(PaymentType.AUTO_TRANSFER.getId())
                .name(PaymentType.AUTO_TRANSFER.getName())
                .build();

        invoiceTypeDto = InvoiceTypeDto.builder()
                .id(InvoiceType.AUTO.getId())
                .name(InvoiceType.AUTO.getName())
                .build();

        paymentStatusDto = PaymentStatusDto.builder()
                .id(PaymentStatus.PENDING.getId())
                .name(PaymentStatus.PENDING.getName())
                .build();

    }

    @Test
    @DisplayName("청구 생성")
    void createInvoice() throws Exception {
        // given
        String url = "/api/v1/invoices";

        CreateInvoiceDto createInvoiceDto = CreateInvoiceDto.builder()
                .contractId(1L)
                .paymentTypeId(2L)
                .chargeAmount(10000L)
                .contractDate(LocalDate.now().plusDays(2))
                .dueDate(LocalDate.now().plusDays(30))
                .build();

        willDoNothing().given(invoiceService).createInvoice(createInvoiceDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .content(objectMapper.writeValueAsString(createInvoiceDto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("invoice/create",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        requestFields(
                                fieldWithPath("contractId")
                                        .description("계약 정보(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("paymentTypeId")
                                        .description("결제 수단(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("chargeAmount")
                                        .description("금액 정보(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("contractDate")
                                        .description("약정일(* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("dueDate")
                                        .description("결제 기한(* required)")
                                        .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("청구 삭제")
    void editInvoice() throws Exception {
        // given
        String url = "/api/v1/invoices/{invoiceId}";

        EditInvoiceDto editInvoiceDto = EditInvoiceDto.builder()
                .paymentTypeId(2L)
                .chargeAmount(10000L)
                .contractDate(LocalDate.now().plusDays(2))
                .dueDate(LocalDate.now().plusDays(30))
                .build();

        willDoNothing().given(invoiceService).editInvoice(anyLong(), eq(editInvoiceDto));

        // when
        ResultActions result = mockMvc.perform(put(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .content(objectMapper.writeValueAsString(editInvoiceDto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("invoice/edit",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("invoiceId").description("청구 ID")),
                        requestFields(
                                fieldWithPath("paymentTypeId")
                                        .description("결제 수단(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("chargeAmount")
                                        .description("금액 정보(* required)")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("contractDate")
                                        .description("약정일(* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("dueDate")
                                        .description("결제 기한(* required)")
                                        .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("청구 삭제")
    void deleteInvoice() throws Exception {
        // given
        String url = "/api/v1/invoices/{invoiceId}";

        willDoNothing().given(invoiceService).deleteInvoice(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("invoice/delete",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("invoiceId").description("청구 ID"))));
    }

    @Test
    @DisplayName("청구 상세 조회")
    void getInvoice() throws Exception {
        // given
        String url = "/api/v1/invoices/{invoiceId}";

        // GetInvoiceDto 객체 생성
        GetInvoiceDto getInvoiceDto = GetInvoiceDto.builder()
                .contractId(1L)
                .invoiceId(1L)
                .paymentType(paymentTypeDto)
                .invoiceType(invoiceTypeDto)
                .paymentStatus(paymentStatusDto)
                .item(itemDto)
                .member(memberDto)
                .chargeAmount(10000L)
                .isSubscription(true)
                .contractDate(LocalDateTime.of(2023, 7, 1, 0, 0))
                .dueDate(LocalDateTime.of(2023, 7, 15, 0, 0))
                .createdAt(LocalDateTime.of(2023, 7, 1, 0, 0))
                .updatedAt(LocalDateTime.of(2023, 7, 10, 0, 0))
                .build();

        given(invoiceService.getInvoice(anyLong())).willReturn(getInvoiceDto);

        // when
        ResultActions result = mockMvc.perform(get(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("invoice/get",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("invoiceId").description("청구 ID")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.contractId").description("계약 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.invoiceId").description("청구서 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.paymentType").description("결제 수단")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.paymentType.id")
                                        .description("결제 수단 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.paymentType.name")
                                        .description("결제 수단명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.invoiceType").description("청구 타입")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.invoiceType.id")
                                        .description("청구 타입 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.invoiceType.name")
                                        .description("청구 타입명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.paymentStatus").description("결제 상태")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.paymentStatus.id")
                                        .description("결제 상태 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.paymentStatus.name")
                                        .description("결제 상태명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.item").description("상품 정보")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.item.itemId").description("상품 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.item.name").description("상품명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.item.price").description("상품 가격")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.item.amount").description("상품 수량")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.member").description("회원 정보")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.member.memberId")
                                        .description("회원 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.member.name").description("회원명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.member.email").description("회원 이메일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.member.phone")
                                        .description("회원 전화번호")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.chargeAmount").description("청구 금액")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.isSubscription")
                                        .description("구독 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.contractDate").description("계약일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.dueDate").description("납부 기한")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.createdAt").description("청구 생성일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.updatedAt").description("청구 수정일")
                                        .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("청구서 목록 조회")
    void getInvoiceList() throws Exception {
        // given
        String url = "/api/v1/invoices";

        GetInvoiceListDto getInvoiceListDto1 = GetInvoiceListDto.builder()
                .invoiceId(1L)
                .contractId(1L)
                .memberName("member1")
                .itemName("item1")
                .chargeAmount(10000L)
                .paymentType(paymentTypeDto)
                .paymentStatus(paymentStatusDto)
                .contractDate(LocalDateTime.of(2023, 7, 1, 0, 0))
                .dueDate(LocalDateTime.of(2023, 7, 15, 0, 0))
                .createdAt(LocalDateTime.of(2023, 7, 1, 0, 0))
                .build();

        GetInvoiceListDto getInvoiceListDto2 = GetInvoiceListDto.builder()
                .invoiceId(2L)
                .contractId(2L)
                .memberName("member2")
                .itemName("item2")
                .chargeAmount(20000L)
                .paymentType(paymentTypeDto)
                .paymentStatus(paymentStatusDto)
                .contractDate(LocalDateTime.of(2023, 8, 1, 0, 0))
                .dueDate(LocalDateTime.of(2023, 8, 15, 0, 0))
                .createdAt(LocalDateTime.of(2023, 8, 1, 0, 0))
                .build();

        List<GetInvoiceListDto> invoiceList = Arrays.asList(getInvoiceListDto1, getInvoiceListDto2);

        PageImpl<GetInvoiceListDto> page = new PageImpl<>(invoiceList, PageRequest.of(0, 20),
                invoiceList.size());
        given(invoiceService.getInvoiceList(anyLong(), anyString(), anyString(), anyLong(), anyLong(),
                any(LocalDate.class), any(LocalDate.class),
                any(LocalDate.class), any(LocalDate.class), any(LocalDate.class), any(LocalDate.class),
                any(Pageable.class)))
                .willReturn(page);

        // when
        ResultActions result = mockMvc.perform(get(url)
                .param("contractId", "1")
                .param("paymentStatusId", "1")
                .param("paymentTypeId", "2")
                .param("startContractDate", "2023-07-01")
                .param("endContractDate", "2023-07-31")
                .param("startDueDate", "2023-07-01")
                .param("endDueDate", "2023-07-31")
                .param("startCreatedAt", "2023-07-01")
                .param("endCreatedAt", "2023-07-31")
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("invoice/get-list",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("contractId").optional()
                                        .description("계약 ID"),
                                parameterWithName("itmeName").optional()
                                        .description("상품명"),
                                parameterWithName("memberName").optional()
                                        .description("회원명"),
                                parameterWithName("paymentStatusId").optional()
                                        .description("결제 상태 ID"),
                                parameterWithName("paymentTypeId").optional()
                                        .description("결제 수단 ID"),
                                parameterWithName("startContractDate").optional()
                                        .description("계약 시작일"),
                                parameterWithName("endContractDate").optional()
                                        .description("계약 종료일"),
                                parameterWithName("startDueDate").optional()
                                        .description("납부 시작일"),
                                parameterWithName("endDueDate").optional()
                                        .description("납부 종료일"),
                                parameterWithName("startCreatedAt").optional()
                                        .description("생성 시작일"),
                                parameterWithName("endCreatedAt").optional()
                                        .description("생성 종료일"),
                                parameterWithName("page").optional()
                                        .description("페이지 번호 (기본값: 0)"),
                                parameterWithName("size").optional()
                                        .description("페이지 크기 (기본값: 20)")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.content").description("페이지 청구서 목록")
                                        .type(JsonFieldType.ARRAY),
                                fieldWithPath("data.content[].invoiceId")
                                        .description("청구서 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.content[].contractId")
                                        .description("계약 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.content[].memberName")
                                        .description("회원명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.content[].itemName")
                                        .description("상품명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.content[].chargeAmount")
                                        .description("청구 금액")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.content[].paymentType")
                                        .description("결제 수단 정보")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.content[].paymentType.id")
                                        .description("결제 수단 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.content[].paymentType.name")
                                        .description("결제 수단명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.content[].paymentStatus")
                                        .description("결제 상태 정보")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.content[].paymentStatus.id")
                                        .description("결제 상태 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.content[].paymentStatus.name")
                                        .description("결제 상태명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.content[].contractDate")
                                        .description("계약일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.content[].dueDate")
                                        .description("납부 기한")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.content[].createdAt")
                                        .description("청구서 생성일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.pageable").description("페이징 정보")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.pageable.sort").description("정렬 정보")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.pageable.sort.empty")
                                        .description("정렬 정보 비어 있음 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pageable.sort.sorted")
                                        .description("정렬 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pageable.sort.unsorted")
                                        .description("정렬되지 않음 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pageable.offset")
                                        .description("페이징 오프셋")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.pageable.pageNumber")
                                        .description("페이지 번호")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.pageable.pageSize")
                                        .description("페이지 크기")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.pageable.paged")
                                        .description("페이징 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.pageable.unpaged")
                                        .description("페이징되지 않음 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.last").description("마지막 페이지 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.totalPages").description("전체 페이지 수")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.totalElements")
                                        .description("전체 요소 수")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.size").description("페이지 크기")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.number").description("현재 페이지 번호")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.sort").description("정렬 정보")
                                        .type(JsonFieldType.OBJECT),
                                fieldWithPath("data.sort.empty")
                                        .description("정렬 정보 비어 있음 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.sort.sorted").description("정렬 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.sort.unsorted")
                                        .description("정렬되지 않음 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.first").description("첫 페이지 여부")
                                        .type(JsonFieldType.BOOLEAN),
                                fieldWithPath("data.numberOfElements")
                                        .description("요소 개수")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.empty").description("비어 있음 여부")
                                        .type(JsonFieldType.BOOLEAN))));
    }
}
