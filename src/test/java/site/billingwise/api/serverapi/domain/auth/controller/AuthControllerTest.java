package site.billingwise.api.serverapi.domain.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import site.billingwise.api.serverapi.domain.auth.dto.request.*;
import site.billingwise.api.serverapi.domain.auth.service.AuthService;
import site.billingwise.api.serverapi.global.mail.EmailService;
import site.billingwise.api.serverapi.global.sms.SmsService;

@WebMvcTest(AuthController.class)
public class AuthControllerTest extends AbstractRestDocsTests {

    @MockBean
    AuthService authService;

    @MockBean
    EmailService emailService;

    @MockBean
    SmsService smsService;

    @Test
    @DisplayName("사용자 등록")
    void register() throws Exception {
        String url = "/api/v1/auth/register";

        RegisterDto registerDto = RegisterDto.builder()
                .authCode("test_code")
                .email("test@gmail.com")
                .password("test1234!")
                .name("홍길동")
                .phone("01011111111")
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

        String email = "test@gmail.com";
        Integer code = 123123;

        EmailCodeDto emailCodeDto = new EmailCodeDto(email, code);

        // given
        willDoNothing().given(authService).authenticateEmail(email, code);

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

    @Test
    @DisplayName("전화번호 인증 코드 전송")
    void sendPhoneCode() throws Exception {
        String url = "/api/v1/auth/phone/code";

        PhoneDto phoneDto = new PhoneDto("01011111111");

        // given
        willDoNothing().given(smsService).sendPhoneCode(phoneDto.getPhone());

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(phoneDto)));

        //then
        result.andExpect(status().isOk()).andDo(document("auth/phone/code",
                requestFields(
                        fieldWithPath("phone").description("전화번호 (* required)").type(JsonFieldType.STRING)
                )));
    }

    @Test
    @DisplayName("전화번호 인증")
    void authenticatePhone() throws Exception {
        String url = "/api/v1/auth/phone/code";

        PhoneCodeDto phoneCodeDto = new PhoneCodeDto("01012341234", 123123);

        // given
        willDoNothing().given(authService).authenticatePhone(phoneCodeDto.getPhone(), phoneCodeDto.getCode());

        // when
        ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(phoneCodeDto)));

        //then
        result.andExpect(status().isOk()).andDo(document("auth/phone/code/authenticate",
                requestFields(
                        fieldWithPath("phone").description("전화번호 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("code").description("코드 (* required)").type(JsonFieldType.NUMBER)

                )));
    }

    @Test
    @DisplayName("이메일 찾기")
    void findEmail() throws Exception {
        String url = "/api/v1/auth/email";

        FindEmailDto findEmailDto = FindEmailDto.builder()
                .name("홍길동")
                .phone("01012341234")
                .code(123123)
                .build();

        EmailDto emailDto = new EmailDto("test@gmail.com");

        // given
        given(authService.findEmail(any(FindEmailDto.class))).willReturn(emailDto);

        // when
        ResultActions result = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(findEmailDto)));

        // then
        result.andExpect(status().isOk())
                .andDo(document("auth/email/find",
                        requestFields(
                                fieldWithPath("name").description("이름 (* required)").type(JsonFieldType.STRING),
                                fieldWithPath("phone").description("전화번호 (* required)").type(JsonFieldType.STRING),
                                fieldWithPath("code").description("인증 코드 (* required)").type(JsonFieldType.NUMBER)
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data.email").description("찾은 이메일").type(JsonFieldType.STRING)
                        )));
    }

    @Test
    @DisplayName("비밀번호 찾기")
    void findPassword() throws Exception {
        String url = "/api/v1/auth/password";

        FindPasswordDto findPasswordDto = FindPasswordDto.builder()
                .email("test@gmail.com")
                .code(123123)
                .newPassword("test1234!")
                .newPasswordCheck("test1234!")
                .build();

        // given
        willDoNothing().given(authService).findPassword(findPasswordDto);

        // when
        ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(findPasswordDto)));

        // then
        result.andExpect(status().isOk()).andDo(document("auth/password/find",
                requestFields(
                        fieldWithPath("email").description("이메일 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("code").description("코드 (* required)").type(JsonFieldType.NUMBER),
                        fieldWithPath("newPassword").description("새 비밀번호 (* required)").type(JsonFieldType.STRING),
                        fieldWithPath("newPasswordCheck").description("새 비밀번호 확인 (* required)")
                                .type(JsonFieldType.STRING)
                )));
    }

}
