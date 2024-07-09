package site.billingwise.api.serverapi.domain.auth.service;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.billingwise.api.serverapi.domain.auth.CustomUserDetails;
import site.billingwise.api.serverapi.domain.auth.dto.LoginDto;
import site.billingwise.api.serverapi.domain.auth.dto.RegisterDto;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.domain.user.repository.UserRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.jwt.JwtProvider;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
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
}
