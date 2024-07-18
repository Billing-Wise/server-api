package site.billingwise.api.serverapi.domain.consent.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.auth.controller.AuthController;
import site.billingwise.api.serverapi.domain.auth.service.AuthService;
import site.billingwise.api.serverapi.domain.consent.dto.request.RegisterConsentDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetConsentDto;
import site.billingwise.api.serverapi.domain.consent.service.ConsentService;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.dto.request.EditItemDto;
import site.billingwise.api.serverapi.domain.item.dto.response.GetItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsentController.class)
public class ConsentControllerTest extends AbstractRestDocsTests {

    @MockBean
    ConsentService consentService;

    @Test
    @DisplayName("동의서 등록")
    void registerConsent() throws Exception {
        String url = "/api/v1/consents/{memberId}";

        // given
        Long memberId = 1L;

        RegisterConsentDto registerConsentDto = RegisterConsentDto.builder()
                .owner("홍길동")
                .bank("신한")
                .number("111222333444")
                .build();

        String registerConsentJsonDto = objectMapper.writeValueAsString(registerConsentDto);

        MockMultipartFile data = new MockMultipartFile("data", "consent", "application/json",
                registerConsentJsonDto.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile signImage = new MockMultipartFile(
                "signImage", "sign.png", "image/png", "consent data".getBytes());

        willDoNothing().given(consentService).registerConsent(memberId, registerConsentDto, signImage);

        // when
        ResultActions result = mockMvc.perform(multipart(url, memberId)
                .file(data)
                .file(signImage)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        result.andExpect(status().isOk())
                .andDo(document("consent/register",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰 (* required)")),
                        requestParts(
                                partWithName("data").description("동의 계좌 정보 (* required)"),
                                partWithName("signImage").description("동의 서명 이미지 (* required)")),
                        requestPartFields("data",
                                fieldWithPath("owner").description("계좌 소유주 (* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("bank").description("은행 (* required)")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("number").description("계좌 번호 (* required)")
                                        .type(JsonFieldType.STRING))));

    }

    @Test
    @DisplayName("동의서 조회")
    void getConsent() throws Exception {
        // given
        String url = "/api/v1/consents/{memberId}";

        GetConsentDto getConsentDto = GetConsentDto.builder()
                .memberId(1L)
                .owner("홍길동")
                .bank("신한")
                .number("111222333444")
                .signUrl("SIGN_URL")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(consentService.getConsent(anyLong())).willReturn(getConsentDto);

        // when
        ResultActions result = mockMvc.perform(get(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("consent/get",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("memberId").description("회원 아이디")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.OBJECT),
                                fieldWithPath("data.memberId").description("회원 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.owner").description("계좌 소유주").type(JsonFieldType.STRING),
                                fieldWithPath("data.bank").description("은행")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.number").description("계좌번호").type(JsonFieldType.STRING),
                                fieldWithPath("data.signUrl").description("서명 이미지 URL")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.createdAt").description("동의서 생성일")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.updatedAt").description("동의서 수정일")
                                        .type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("동의정보 수정")
    void editConsent() throws Exception {

        // given
        String url = "/api/v1/consents/{memberId}";

        RegisterConsentDto editConsentDto = RegisterConsentDto.builder()
                .owner("홍길동")
                .bank("신한")
                .number("111222333444")
                .build();

        willDoNothing().given(consentService).editConsent(anyLong(), eq(editConsentDto));

        // when
        ResultActions result = mockMvc.perform(put(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editConsentDto)));

        // then
        result.andExpect(status().isOk()).andDo(document("consent/edit",
                requestCookies(
                        cookieWithName("access").description("엑세스 토큰")),
                pathParameters(
                        parameterWithName("memberId").description("회원 아이디")),
                requestFields(
                        fieldWithPath("owner").description("계좌 소유주 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("bank").description("은행 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("number").description("계좌번호 (* required)").type(JsonFieldType.STRING))));
    }

    @Test
    @DisplayName("동의 서명 수정")
    void editConsentImage() throws Exception {
        // given
        String url = "/api/v1/consents/{memberId}/image";

        MockMultipartFile signImage = new MockMultipartFile(
                "signImage", "sign.png", "image/png", "consent data".getBytes());

        willDoNothing().given(consentService).editConsentSignImage(anyLong(), eq(signImage));

        // when
        ResultActions result = mockMvc.perform(multipart(url, 1L)
                .file(signImage)
                .cookie(new Cookie("access", "ACCESS_TOKEN"))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }));

        // then
        result.andExpect(status().isOk())
                .andDo(document("consent/edit-image",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("memberId").description("회원 아이디")),
                        requestParts(
                                partWithName("signImage").description("서명 이미지"))));
    }

    @Test
    @DisplayName("동의정보 삭제")
    void deleteConsent() throws Exception {

        // given
        String url = "/api/v1/consents/{memberId}";

        willDoNothing().given(consentService).deleteConsent(1L);

        // when
        ResultActions result = mockMvc.perform(delete(url, 1L)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("consent/delete",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        pathParameters(
                                parameterWithName("memberId").description("회원 아이디"))));
    }

}
