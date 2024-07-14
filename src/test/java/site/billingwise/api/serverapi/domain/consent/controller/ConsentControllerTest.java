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
import site.billingwise.api.serverapi.domain.consent.service.ConsentService;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
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

}
