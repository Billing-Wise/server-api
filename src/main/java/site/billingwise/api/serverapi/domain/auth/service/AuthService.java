package site.billingwise.api.serverapi.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import site.billingwise.api.serverapi.domain.auth.CustomUserDetails;
import site.billingwise.api.serverapi.domain.auth.dto.request.LoginDto;
import site.billingwise.api.serverapi.domain.auth.dto.request.RegisterDto;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.domain.user.repository.UserRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.jwt.JwtProvider;
import site.billingwise.api.serverapi.global.jwt.RefreshToken;
import site.billingwise.api.serverapi.global.jwt.RefreshTokenRedisRepository;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
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

    public void checkEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new GlobalException(FailureInfo.ALREADY_EXIST_EMAIL);
        }
    }
}
