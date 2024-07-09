package site.billingwise.api.serverapi.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.billingwise.api.serverapi.domain.auth.dto.LoginDto;
import site.billingwise.api.serverapi.domain.auth.dto.RegisterDto;
import site.billingwise.api.serverapi.domain.auth.service.AuthService;
import site.billingwise.api.serverapi.global.response.BaseResponse;
import site.billingwise.api.serverapi.global.response.DataResponse;
import site.billingwise.api.serverapi.global.response.info.SuccessInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;
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

}
