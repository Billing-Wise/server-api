package site.billingwise.api.serverapi.domain.invoice.controller;

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
import java.time.LocalDate;

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
import site.billingwise.api.serverapi.domain.invoice.dto.request.CreateInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.service.InvoiceService;

@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc
public class InvoiceControllerTest extends AbstractRestDocsTests {

    @MockBean
    InvoiceService invoiceService;

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
    void testDeleteInvoice() {

    }

    @Test
    void testEditInvoice() {

    }

    @Test
    void testGetInvoice() {

    }

    @Test
    void testGetInvoiceList() {

    }
}
