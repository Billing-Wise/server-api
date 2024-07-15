package site.billingwise.api.serverapi.domain.consent.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetBasicItemDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetContractInfoDto;
import site.billingwise.api.serverapi.domain.consent.service.EasyConsentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EasyConsentController.class)
public class EasyConsentControllerTest extends AbstractRestDocsTests {

    @MockBean
    private EasyConsentService easyConsentService;

    @Test
    @DisplayName("비회원 간편동의 상품목록 조회")
    void getBasicItemList() throws Exception {
        // given
        String url = "/api/v1/easy-consent/non-member/items";

        Long clientId = 1L;

        List<GetBasicItemDto> getBasicItemDtoList = Arrays.asList(
                GetBasicItemDto.builder()
                        .id(1L)
                        .name("item1")
                        .price(10000L)
                        .imageUrl("IMAGE_URL")
                        .build(),
                GetBasicItemDto.builder()
                        .id(2L)
                        .name("item2")
                        .price(20000L)
                        .imageUrl("IMAGE_URL")
                        .build()
        );

        given(easyConsentService.getBasicItemList(anyLong())).willReturn(getBasicItemDtoList);

        // when
        ResultActions result = mockMvc.perform(get(url)
                .param("clientId", clientId.toString()));

        // then
        result.andExpect(status().isOk())
                .andDo(document("easy-consent/non-member/items/get-list",
                        queryParameters(
                                parameterWithName("clientId").description("고객 아이디")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.ARRAY),
                                fieldWithPath("data[].id").description("상품 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].name").description("상품명").type(JsonFieldType.STRING),
                                fieldWithPath("data[].price").description("상품 가격").type(JsonFieldType.NUMBER),
                                fieldWithPath("data[].imageUrl").description("상품 이미지 URL")
                                        .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("회원 간편동의 계약정보 조회")
    void getContractInfo() throws Exception {
        // given
        String url = "/api/v1/easy-consent/member/contracts/{contractId}";

        GetContractInfoDto getContractInfoDto = GetContractInfoDto.builder()
                .contractId(1L)
                .memberId(1L)
                .memberName("홍길동")
                .memberEmail("test@gmail.com")
                .memberPhone("01012341234")
                .itemId(1L)
                .itemName("item1")
                .itemAmount(3)
                .totalPrice(30000L)
                .contractCycle(3)
                .build();

        given(easyConsentService.getContractInfo(anyLong())).willReturn(getContractInfoDto);

        // when
        ResultActions result = mockMvc.perform(get(url, 1L));

        // then
        result.andExpect(status().isOk())
                .andDo(document("easy-consent/member/contract/get",
                        pathParameters(
                                parameterWithName("contractId").description("계약 아이디")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.OBJECT),
                                fieldWithPath("data.contractId").description("계약 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.memberId").description("회원 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.memberId").description("회원 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.memberName").description("회원 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.memberEmail").description("회원 이메일").type(JsonFieldType.STRING),
                                fieldWithPath("data.memberPhone").description("회원 전화번호").type(JsonFieldType.STRING),
                                fieldWithPath("data.itemId").description("상품 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.itemName").description("상품명").type(JsonFieldType.STRING),
                                fieldWithPath("data.itemAmount").description("상품 개수").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.totalPrice").description("총 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.contractCycle").description("약정일 주기").type(JsonFieldType.NUMBER))));
    }
}
