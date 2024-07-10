package site.billingwise.api.serverapi.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billingwise.api.serverapi.domain.auth.dto.request.EmailDto;
import site.billingwise.api.serverapi.domain.auth.dto.request.LoginDto;
import site.billingwise.api.serverapi.domain.auth.dto.request.RegisterDto;
import site.billingwise.api.serverapi.domain.auth.service.AuthService;
import site.billingwise.api.serverapi.global.mail.MailService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final MailService mailService;
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/register")
    public BaseResponse register(@Valid @RequestBody RegisterDto registerDto) {
        authService.register(registerDto);
        return new BaseResponse(SuccessInfo.REGISTER);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestBody LoginDto loginDto) {
        authService.login(loginDto);
        return new BaseResponse(SuccessInfo.LOGIN);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public BaseResponse logout() {
        authService.logout();
        return new BaseResponse(SuccessInfo.LOGOUT);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reissue")
    public BaseResponse reissue() {
        authService.reissue();
        return new BaseResponse(SuccessInfo.REISSUE);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/email/duplication")
    public BaseResponse checkEmailDuplication(@Valid @RequestBody EmailDto emailDto) {
        authService.checkEmailDuplication(emailDto.getEmail());
        return new BaseResponse(SuccessInfo.AVAILABLE_EMAIL);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/email/code")
    public BaseResponse sendEmailCode(@Valid @RequestBody EmailDto emailDto) {
        mailService.sendMailCode(emailDto.getEmail());
        return new BaseResponse(SuccessInfo.SEND_MAIL_CODE);
    }

}
