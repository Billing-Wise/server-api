package site.billingwise.api.serverapi.domain.user.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.consent.controller.EasyConsentController;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetConsentDto;
import site.billingwise.api.serverapi.domain.consent.service.ConsentService;
import site.billingwise.api.serverapi.domain.user.dto.response.GetCurrentUserDto;
import site.billingwise.api.serverapi.domain.user.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest extends AbstractRestDocsTests {

    @MockBean
    UserService userService;

    @Test
    @DisplayName("동의서 조회")
    void getConsent() throws Exception {
        // given
        String url = "/api/v1/users/current";

        GetConsentDto getConsentDto = GetConsentDto.builder()
                .memberId(1L)
                .owner("홍길동")
                .bank("신한")
                .number("111222333444")
                .signUrl("SIGN_URL")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        GetCurrentUserDto dto = GetCurrentUserDto.builder()
                .clientId(1L)
                .clientName("KOSA")
                .clientPhone("01011112222")
                .userId(1L)
                .userEmail("test@gmail.com")
                .userName("홍길동")
                .userPhone("01033334444")
                .build();

        given(userService.getCurrentUser()).willReturn(dto);

        // when
        ResultActions result = mockMvc.perform(get(url)
                .cookie(new Cookie("access", "ACCESS_TOKEN")));

        // then
        result.andExpect(status().isOk())
                .andDo(document("user/current/get",
                        requestCookies(
                                cookieWithName("access").description("엑세스 토큰")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.OBJECT),
                                fieldWithPath("data.clientId").description("고객 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.clientName").description("고객명").type(JsonFieldType.STRING),
                                fieldWithPath("data.clientPhone").description("고객 전화번호")
                                        .type(JsonFieldType.STRING),
                                fieldWithPath("data.userId").description("사용자 아이디").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.userEmail").description("사용자 이메일").type(JsonFieldType.STRING),
                                fieldWithPath("data.userName").description("사용자명").type(JsonFieldType.STRING),
                                fieldWithPath("data.userPhone").description("사용자 전화번호").type(JsonFieldType.STRING))));
    }

}
