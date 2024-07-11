package site.billingwise.api.serverapi.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.billingwise.api.serverapi.domain.auth.dto.request.*;
import site.billingwise.api.serverapi.domain.auth.service.AuthService;
import site.billingwise.api.serverapi.global.mail.EmailService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;
import site.billingwise.api.serverapi.global.sms.SmsService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;
    private final SmsService smsService;

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
        emailService.sendMailCode(emailDto.getEmail());
        return new BaseResponse(SuccessInfo.SEND_MAIL_CODE);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/email/code")
    public BaseResponse authenticateEmail(@Valid @RequestBody EmailCodeDto emailCodeDto) {
        authService.authenticateEmail(emailCodeDto.getEmail(), emailCodeDto.getCode());
        return new BaseResponse(SuccessInfo.AUTHENTICATE_EMAIL);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/phone/code")
    public BaseResponse sendPhoneCode(@Valid @RequestBody PhoneDto phoneDto) {
        smsService.sendPhoneCode(phoneDto.getPhone());
        return new BaseResponse(SuccessInfo.SEND_PHONE_CODE);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/phone/code")
    public BaseResponse authenticatePhone(@Valid @RequestBody PhoneCodeDto phoneCodeDto) {
        authService.authenticatePhone(phoneCodeDto.getPhone(), phoneCodeDto.getCode());
        return new BaseResponse(SuccessInfo.AUTHENTICATE_PHONE);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/email")
    public DataResponse<EmailDto> findEmail(@Valid @RequestBody FindEmailDto findEmailDto) {
        return new DataResponse<>(SuccessInfo.FIND_EMAIL, authService.findEmail(findEmailDto));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/password")
    public BaseResponse findPassword(@Valid @RequestBody FindPasswordDto findPasswordDto) {
        authService.findPassword(findPasswordDto);
        return new BaseResponse(SuccessInfo.FIND_PASSWORD);
    }

}
