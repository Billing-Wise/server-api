package site.billingwise.api.serverapi.domain.payment.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.servlet.http.Cookie;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.consent.controller.EasyConsentController;
import site.billingwise.api.serverapi.domain.consent.dto.request.ConsentWithNonMemberDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetContractInfoDto;
import site.billingwise.api.serverapi.domain.consent.service.EasyConsentService;
import site.billingwise.api.serverapi.domain.invoice.dto.response.GetInvoiceDto;
import site.billingwise.api.serverapi.domain.payment.PaymentMethod;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayAccountDto;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayCardDto;
import site.billingwise.api.serverapi.domain.payment.dto.response.GetPayerPayInvoiceDto;
import site.billingwise.api.serverapi.domain.payment.dto.response.GetPaymentAccountDto;
import site.billingwise.api.serverapi.domain.payment.dto.response.GetPaymentCardDto;
import site.billingwise.api.serverapi.domain.payment.service.PaymentService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest extends AbstractRestDocsTests {

    @MockBean
    private PaymentService paymentService;

    @Test
    @DisplayName("카드 납부자 결제")
    void payerPayCard() throws Exception {
        // given
        String url = "/api/v1/payments/payer-pay/card";

        Long invoiceId = 1L;

        PayerPayCardDto payerPayCardDto = PayerPayCardDto.builder()
                .number("1234123412341234")
                .company("신한카드")
                .owner("홍길동")
                .build();

        // given
        willDoNothing().given(paymentService).payerPayCard(invoiceId, payerPayCardDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .queryParam("invoiceId", String.valueOf(invoiceId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payerPayCardDto)));

        // then
        result.andExpect(status().isOk()).andDo(document("payer-payment/card",
                queryParameters(
                        parameterWithName("invoiceId").description("청구 아이디")),
                requestFields(
                        fieldWithPath("owner").description("카드 소유주 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("company").description("카드사 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("number").description("카드번호 (* required)").type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("계좌 납부자 결제")
    void payerPayAccount() throws Exception {
        // given
        String url = "/api/v1/payments/payer-pay/account";

        Long invoiceId = 1L;

        PayerPayAccountDto payerPayAccountDto = PayerPayAccountDto.builder()
                .number("1234123412341234")
                .bank("신한")
                .owner("홍길동")
                .build();

        // given
        willDoNothing().given(paymentService).payerPayAccount(invoiceId, payerPayAccountDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .queryParam("invoiceId", String.valueOf(invoiceId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payerPayAccountDto)));

        // then
        result.andExpect(status().isOk()).andDo(document("payer-payment/account",
                queryParameters(
                        parameterWithName("invoiceId").description("청구 아이디")),
                requestFields(
                        fieldWithPath("owner").description("계좌 소유주 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("bank").description("은행 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("number").description("계좌번호 (* required)").type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("납부자결제 청구정보 조회")
    void getPayerPayInvoiceInfo() throws Exception {
        // given
        String url = "/api/v1/payments/payer-pay/invoices/{invoiceId}";

        GetPayerPayInvoiceDto dto = GetPayerPayInvoiceDto.builder()
                .memberName("홍길동")
                .memberEmail("test@gmail.com")
                .memberPhone("01012341234")
                .itemName("item1")
                .itemAmount(3)
                .totalPrice(30000L)
                .contractDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now())
                .build();

        given(paymentService.getPayerPayInvoice(anyLong())).willReturn(dto);

        // when
        ResultActions result = mockMvc.perform(get(url, 1L));

        // then
        result.andExpect(status().isOk())
                .andDo(document("payer-payment/invoice/get",
                        pathParameters(
                                parameterWithName("invoiceId").description("청구 아이디")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.OBJECT),
                                fieldWithPath("data.memberName").description("회원 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.memberEmail").description("회원 이메일").type(JsonFieldType.STRING),
                                fieldWithPath("data.memberPhone").description("회원 전화번호").type(JsonFieldType.STRING),
                                fieldWithPath("data.itemName").description("상품명").type(JsonFieldType.STRING),
                                fieldWithPath("data.itemAmount").description("상품 개수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.totalPrice").description("총 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.contractDate").description("약정일").type(JsonFieldType.STRING),
                                fieldWithPath("data.dueDate").description("결제기한").type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("납부 내역 취소")
    void deletePayment() throws Exception {
        // given
        String url = "/api/v1/payments/{invoiceId}";

        willDoNothing().given(paymentService).deletePayment(anyLong());

        // when
        ResultActions result = mockMvc.perform(delete(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("payment/delete",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("invoiceId").description("청구 ID"))));
    }

    @Test
    @DisplayName("납부 내역 조회 - 계좌")
    void getPaymentAccount() throws Exception {
        // given
        String url = "/api/v1/payments/{invoiceId}";

        // GetInvoiceDto 객체 생성
        GetPaymentAccountDto getPaymentAccountDto = GetPaymentAccountDto.builder()
                .invoiceId(1L)
                .payAmount(10000L)
                .paymentMethod(PaymentMethod.ACCOUNT)
                .createAt(LocalDateTime.now())
                .number("111111")
                .bank("국민")
                .owner("홍길동")
                .build();

        given(paymentService.getPayment(anyLong())).willReturn(getPaymentAccountDto);

        // when
        ResultActions result = mockMvc.perform(get(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("payment/get-account",
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
                                fieldWithPath("data.invoiceId").description("청구 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.payAmount").description("납부금")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.paymentMethod").description("납부 수단")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.createAt").description("납부일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.number").description("계좌 번호")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.bank").description("은행명")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.owner").description("계좌주")
                                        .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("납부 내역 조회 - 카드")
    void getPaymentCard() throws Exception {
        // given
        String url = "/api/v1/payments/{invoiceId}";

        // GetInvoiceDto 객체 생성
        GetPaymentCardDto getPaymentCardDto = GetPaymentCardDto.builder()
                .invoiceId(1L)
                .payAmount(10000L)
                .paymentMethod(PaymentMethod.ACCOUNT)
                .createAt(LocalDateTime.now())
                .number("111111")
                .company("삼성")
                .owner("홍길동")
                .build();

        given(paymentService.getPayment(anyLong())).willReturn(getPaymentCardDto);

        // when
        ResultActions result = mockMvc.perform(get(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("payment/get-card",
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
                                fieldWithPath("data.invoiceId").description("청구 ID")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.payAmount").description("납부금")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data.paymentMethod").description("납부 수단")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.createAt").description("납부일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.number").description("카드 번호")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.company").description("카드사")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.owner").description("카드 소유자명")
                                        .type(JsonFieldType.STRING))));
    }

}
