package site.billingwise.api.serverapi.domain.auth.service;

import jakarta.persistence.Table;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import site.billingwise.api.serverapi.domain.user.CustomUserDetails;
import site.billingwise.api.serverapi.domain.auth.dto.request.*;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.domain.user.repository.UserRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.jwt.JwtProvider;
import site.billingwise.api.serverapi.global.jwt.RefreshToken;
import site.billingwise.api.serverapi.global.jwt.RefreshTokenRedisRepository;
import site.billingwise.api.serverapi.global.mail.EmailCode;
import site.billingwise.api.serverapi.global.mail.EmailCodeRedisRepository;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.sms.PhoneCode;
import site.billingwise.api.serverapi.global.sms.PhoneCodeRedisRepository;
import site.billingwise.api.serverapi.global.util.CookieUtil;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final EmailCodeRedisRepository emailCodeRedisRepository;
    private final PhoneCodeRedisRepository phoneCodeRedisRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public void register(RegisterDto registerDto) {
        Client client = clientRepository.findByAuthCode(registerDto.getAuthCode())
                .orElseThrow(() -> new GlobalException(FailureInfo.UNAUTHORIZED_AUTH_CODE));

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new GlobalException(FailureInfo.ALREADY_EXIST_USER);
        }
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userRepository.save(registerDto.toEntity(client));
    }

    public void login(LoginDto loginDto) {
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        } catch (Exception ex) {
            log.info(ex.getMessage());
            throw new GlobalException(FailureInfo.WRONG_LOGIN_INFO);
        }

        jwtProvider.addAccessToken(authentication, response);
        jwtProvider.addRefreshToken(authentication, response);

    }

    public void logout() {
        CookieUtil.deleteCookie(request, response, "access");
        CookieUtil.deleteCookie(request, response, "refresh");

        Optional<User> optionalUser = SecurityUtil.getCurrentUser();

        if (optionalUser.isPresent()) {
            refreshTokenRedisRepository.deleteById(optionalUser.get().getId());
        }
    }

    public void reissue() {
        Cookie refreshCookie = CookieUtil.getCookie(request, "refresh");

        if (refreshCookie == null || !StringUtils.hasText(refreshCookie.getValue())) {
            throw new GlobalException(FailureInfo.REFRESH_TOKEN_NULL);
        }

        String refreshToken = refreshCookie.getValue();

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new GlobalException(FailureInfo.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long id = customUserDetails.getUser().getId();

        RefreshToken savedRefreshToken = refreshTokenRedisRepository.findById(id)
                .orElseThrow(() -> new GlobalException(FailureInfo.INVALID_REFRESH_TOKEN));

        if (!savedRefreshToken.getToken().equals(refreshToken)) {
            throw new GlobalException(FailureInfo.INVALID_REFRESH_TOKEN);
        }

        jwtProvider.addAccessToken(authentication, response);
        jwtProvider.addRefreshToken(authentication, response);
    }

    @Transactional(readOnly = true)
    public void checkEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new GlobalException(FailureInfo.ALREADY_EXIST_EMAIL);
        }
    }


    public void authenticateEmail(String email, Integer code) {
        EmailCode emailCode = emailCodeRedisRepository.findById(email)
                .orElseThrow(() -> new GlobalException(FailureInfo.INVALID_MAIL_CODE));

        if (!emailCode.getCode().equals(code)) {
            throw new GlobalException(FailureInfo.INVALID_MAIL_CODE);
        }
    }

    public void authenticatePhone(String phone, Integer code) {
        PhoneCode phoneCode = phoneCodeRedisRepository.findById(phone)
                .orElseThrow(() -> new GlobalException(FailureInfo.INVALID_PHONE_CODE));

        if (!phoneCode.getCode().equals(code)) {
            throw new GlobalException(FailureInfo.INVALID_PHONE_CODE);
        }
    }

    @Transactional(readOnly = true)
    public EmailDto findEmail(FindEmailDto findEmailDto) {

        authenticatePhone(findEmailDto.getPhone(), findEmailDto.getCode());

        User user = userRepository.findByNameAndPhone(findEmailDto.getName(), findEmailDto.getPhone())
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        return EmailDto.builder()
                .email(user.getEmail())
                .build();

    }

    @Transactional
    public void findPassword(FindPasswordDto findPasswordDto) {

        if (!findPasswordDto.getNewPassword().equals(findPasswordDto.getNewPasswordCheck())) {
            throw new GlobalException(FailureInfo.NEW_PASSWORD_MISMATCH);
        }

        authenticateEmail(findPasswordDto.getEmail(), findPasswordDto.getCode());

        User user = userRepository.findByEmail(findPasswordDto.getEmail())
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        user.setPassword(passwordEncoder.encode(findPasswordDto.getNewPassword()));
    }
}
