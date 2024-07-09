package site.billingwise.api.serverapi.domain.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import site.billingwise.api.serverapi.docs.restdocs.AbstractRestDocsTests;
import site.billingwise.api.serverapi.domain.auth.controller.AuthController;
import site.billingwise.api.serverapi.domain.auth.dto.LoginDto;
import site.billingwise.api.serverapi.domain.auth.dto.RegisterDto;
import site.billingwise.api.serverapi.domain.auth.service.AuthService;

@WebMvcTest(AuthController.class)
public class AuthControllerTest extends AbstractRestDocsTests {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

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
}
