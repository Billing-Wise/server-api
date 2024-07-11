package site.billingwise.api.serverapi.domain.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.auth.dto.request.EmailCodeDto;
import site.billingwise.api.serverapi.domain.auth.dto.request.EmailDto;
import site.billingwise.api.serverapi.domain.auth.dto.request.LoginDto;
import site.billingwise.api.serverapi.domain.auth.dto.request.RegisterDto;
import site.billingwise.api.serverapi.domain.auth.service.AuthService;
import site.billingwise.api.serverapi.global.mail.EmailService;

@WebMvcTest(AuthController.class)
public class AuthControllerTest extends AbstractRestDocsTests {

    @MockBean
    AuthService authService;

    @MockBean
    EmailService emailService;

    @Test
    @DisplayName("사용자 등록")
    void register() throws Exception {
        String url = "/api/v1/auth/register";

        RegisterDto registerDto = RegisterDto.builder()
                .authCode("test_code")
                .email("test@gmail.com")
                .password("test1234!")
                .name("홍길동")
                .phone("010-1111-1111")
                .build();

        // given
        willDoNothing().given(authService).register(registerDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)));

        //then
        result.andExpect(status().isOk()).andDo(document("auth/register",
            requestFields(
                    fieldWithPath("authCode").description("고객 인증 코드 (* required)").type(JsonFieldType.STRING),
                    fieldWithPath("email").description("이메일 (* required)").type(JsonFieldType.STRING),
                    fieldWithPath("password").description("비밀번호 (* required)").type(JsonFieldType.STRING),
                    fieldWithPath("name").description("이름 (* required)").type(JsonFieldType.STRING),
                    fieldWithPath("phone").description("전화번호 (* required)").type(JsonFieldType.STRING)
            )));
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {
        String url = "/api/v1/auth/login";

        LoginDto loginDto = LoginDto.builder()
                .email("test@gmail.com")
                .password("test1234!")
                .build();

        // given
        willDoNothing().given(authService).login(loginDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)));

        //then
        result.andExpect(status().isOk()).andDo(document("auth/login",
                requestFields(
                        fieldWithPath("email").description("이메일 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("password").description("비밀번호 (* required)").type(JsonFieldType.STRING)
                )));
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        String url = "/api/v1/auth/logout";

        // given
        willDoNothing().given(authService).logout();

        // when
        ResultActions result = mockMvc.perform(post(url));

        //then
        result.andExpect(status().isOk()).andDo(document("auth/logout"));
    }

    @Test
    @DisplayName("토큰 재발급")
    void reissue() throws Exception {
        String url = "/api/v1/auth/reissue";

        // given
        willDoNothing().given(authService).reissue();

        // when
        ResultActions result = mockMvc.perform(post(url)
                .cookie(new Cookie("refresh", "REFRESH_TOKEN")));


        //then
        result.andExpect(status().isOk()).andDo(document("auth/reissue",
                requestCookies(cookieWithName("refresh").description("리프레시 토큰"))));
    }

    @Test
    @DisplayName("이메일 중복 체크")
    void checkEmailDuplication() throws Exception {
        String url = "/api/v1/auth/email/duplication";

        EmailDto emailDto = new EmailDto("test@gmail.com");

        // given
        willDoNothing().given(authService).checkEmailDuplication(emailDto.getEmail());

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailDto)));

        //then
        result.andExpect(status().isOk()).andDo(document("auth/email/duplication",
                requestFields(
                        fieldWithPath("email").description("이메일 (* required)").type(JsonFieldType.STRING)
                )));
    }

    @Test
    @DisplayName("이메일 인증 코드 전송")
    void sendEmailCode() throws Exception {
        String url = "/api/v1/auth/email/code";

        EmailDto emailDto = new EmailDto("test@gmail.com");

        // given
        willDoNothing().given(emailService).sendMailCode(emailDto.getEmail());

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailDto)));

        //then
        result.andExpect(status().isOk()).andDo(document("auth/email/code",
                requestFields(
                        fieldWithPath("email").description("이메일 (* required)").type(JsonFieldType.STRING)
                )));
    }

    @Test
    @DisplayName("이메일 인증")
    void authenticateEmail() throws Exception {
        String url = "/api/v1/auth/email/code";

        EmailCodeDto emailCodeDto = new EmailCodeDto("test@gmail.com", 123123);

        // given
        willDoNothing().given(authService).authenticateEmail(emailCodeDto);

        // when
        ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailCodeDto)));

        //then
        result.andExpect(status().isOk()).andDo(document("auth/email/code/authenticate",
                requestFields(
                        fieldWithPath("email").description("이메일 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("code").description("코드 (* required)").type(JsonFieldType.NUMBER)

                )));
    }
}
