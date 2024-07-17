package site.billingwise.api.serverapi.domain.payment.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.consent.controller.EasyConsentController;
import site.billingwise.api.serverapi.domain.consent.dto.request.ConsentWithNonMemberDto;
import site.billingwise.api.serverapi.domain.consent.service.EasyConsentService;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayAccountDto;
import site.billingwise.api.serverapi.domain.payment.dto.request.PayerPayCardDto;
import site.billingwise.api.serverapi.domain.payment.service.PaymentService;

import java.nio.charset.StandardCharsets;

import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
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

        //then
        result.andExpect(status().isOk()).andDo(document("payer-payment/card",
                queryParameters(
                        parameterWithName("invoiceId").description("청구 아이디")),
                requestFields(
                        fieldWithPath("owner").description("카드 소유주 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("company").description("카드사 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("number").description("카드번호 (* required)").type(JsonFieldType.STRING)
                )));
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

        //then
        result.andExpect(status().isOk()).andDo(document("payer-payment/account",
                queryParameters(
                        parameterWithName("invoiceId").description("청구 아이디")),
                requestFields(
                        fieldWithPath("owner").description("계좌 소유주 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("bank").description("은행 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("number").description("계좌번호 (* required)").type(JsonFieldType.STRING)
                )));
    }

}
