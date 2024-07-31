package site.billingwise.api.serverapi.domain.stats.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
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
import site.billingwise.api.serverapi.domain.stats.InvoiceStatsType;
import site.billingwise.api.serverapi.domain.stats.dto.InvoiceStatsDto;
import site.billingwise.api.serverapi.domain.stats.service.InvoiceStatsService;

@WebMvcTest(InvoiceStatsController.class)
@AutoConfigureMockMvc
public class InvoiceStatsControllerTest extends AbstractRestDocsTests {

    @MockBean
    InvoiceStatsService invoiceStatsService;

    @Test
    @DisplayName("통계 데이터 조회")
    void getInvoiceStats() throws Exception {
        // given
        String url = "/api/v1/stats/{typeId}";

        InvoiceStatsDto invoiceStatsDto = InvoiceStatsDto.builder()
                .id(1L)
                .date(LocalDateTime.now())
                .totalInvoiced(1000L)
                .totalCollected(500L)
                .outstanding(500L)
                .year(2024)
                .month(7)
                .week(31)
                .type(InvoiceStatsType.MONTHLY)
                .build();

        List<InvoiceStatsDto> invoiceStatsDtoList = Arrays.asList(invoiceStatsDto);

        given(invoiceStatsService.getInvoiceStats(anyLong(), anyInt(), anyInt())).willReturn(invoiceStatsDtoList);

        // when
        ResultActions result = mockMvc.perform(get(url, 1L)
                .param("year", "2024")
                .param("month", "7")
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(document("stats/get-invoice-stats",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("typeId").description("통계 타입 ID")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.ARRAY),
                                fieldWithPath("data[].id").description("통계 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].date").description("통계 날짜").type(JsonFieldType.STRING),
                                fieldWithPath("data[].totalInvoiced").description("총 청구 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].totalCollected").description("총 수집 금액")
                                        .type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].outstanding").description("미수금").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].year").description("년도").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].month").description("월").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].week").description("주").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].type").description("통계 타입").type(JsonFieldType.STRING))));
    }
}
