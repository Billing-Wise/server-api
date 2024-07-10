package site.billingwise.api.serverapi.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthService authService;

    MockedStatic<SecurityUtil> mockSecurityUtil;
    MockedStatic<CookieUtil> mockCookieUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockSecurityUtil = mockStatic(SecurityUtil.class);
        mockCookieUtil = mockStatic(CookieUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockSecurityUtil.close();
        mockCookieUtil.close();
    }

    @Test
    void register_Success() {
        RegisterDto registerDto = RegisterDto.builder()
                .authCode("test_code")
                .email("test@gmail.com")
                .password("test1234!")
                .name("홍길동")
                .phone("010-1111-1111")
                .build();

        Client client = Client.builder().build();
        when(clientRepository.findByAuthCode(anyString())).thenReturn(Optional.of(client));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");

        authService.register(registerDto);

        verify(clientRepository, times(1)).findByAuthCode(anyString());
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void register_AuthCodeNotFound() {
        RegisterDto registerDto = RegisterDto.builder()
                .authCode("test_code")
                .email("test@gmail.com")
                .password("test1234!")
                .name("홍길동")
                .phone("010-1111-1111")
                .build();

        when(clientRepository.findByAuthCode(anyString())).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> authService.register(registerDto));
        assertEquals(FailureInfo.UNAUTHORIZED_AUTH_CODE, exception.getFailureInfo());

        verify(clientRepository, times(1)).findByAuthCode(anyString());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_EmailAlreadyExists() {
        RegisterDto registerDto = RegisterDto.builder()
                .authCode("test_code")
                .email("test@gmail.com")
                .password("test1234!")
                .name("홍길동")
                .phone("010-1111-1111")
                .build();

        Client client = Client.builder().build();
        when(clientRepository.findByAuthCode(anyString())).thenReturn(Optional.of(client));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        GlobalException exception = assertThrows(GlobalException.class, () -> authService.register(registerDto));
        assertEquals(FailureInfo.ALREADY_EXIST_USER, exception.getFailureInfo());

        verify(clientRepository, times(1)).findByAuthCode(anyString());
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_Success() {
        LoginDto loginDto = LoginDto.builder()
                .email("test@gmail.com")
                .password("test1234!")
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        authService.login(loginDto);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtProvider, times(1)).addAccessToken(authentication, response);
        verify(jwtProvider, times(1)).addRefreshToken(authentication, response);
    }

    @Test
    void login_Failure() {
        LoginDto loginDto = LoginDto.builder()
                .email("test@gmail.com")
                .password("wrong_password")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") { });

        GlobalException exception = assertThrows(GlobalException.class, () -> authService.login(loginDto));
        assertEquals(FailureInfo.WRONG_LOGIN_INFO, exception.getFailureInfo());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtProvider, never()).addAccessToken(any(Authentication.class), any(HttpServletResponse.class));
        verify(jwtProvider, never()).addRefreshToken(any(Authentication.class), any(HttpServletResponse.class));
    }

    @Test
    void logout_Success() {

        Long userId = 1L;

        User mockUser = User.builder().id(userId).build();
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));

        mockCookieUtil.when(() -> CookieUtil.deleteCookie(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class),
                anyString()
        )).thenAnswer(invocation -> null);

        authService.logout();

        verify(refreshTokenRedisRepository, times(1)).deleteById(userId);

        mockCookieUtil.verify(() -> CookieUtil.deleteCookie(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class),
                eq("access")),
                times(1));
        mockCookieUtil.verify(() -> CookieUtil.deleteCookie(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class),
                eq("refresh")),
                times(1));
    }

    @Test
    void logout_NoCurrentUser() {
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.empty());

        mockCookieUtil.when(() -> CookieUtil.deleteCookie(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class),
                anyString()
        )).thenAnswer(invocation -> null);

        authService.logout();

        verify(refreshTokenRedisRepository, never()).deleteById(any(Long.class));

        mockCookieUtil.verify(() -> CookieUtil.deleteCookie(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class),
                eq("access")),
                times(1));
        mockCookieUtil.verify(() -> CookieUtil.deleteCookie(
                any(HttpServletRequest.class),
                any(HttpServletResponse.class),
                eq("refresh")),
                times(1));

    }

    @Test
    void reissue_Success() {
        String refreshTokenValue = "validRefreshToken";
        Cookie refreshCookie = new Cookie("refresh", refreshTokenValue);

        mockCookieUtil.when(() -> CookieUtil.getCookie(request, "refresh")).thenReturn(refreshCookie);

        when(jwtProvider.validateToken(refreshTokenValue)).thenReturn(true);

        User mockUser = User.builder().id(1L).build();
        CustomUserDetails customUserDetails = new CustomUserDetails(mockUser);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(jwtProvider.getAuthentication(refreshTokenValue)).thenReturn(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token(refreshTokenValue)
                .build();
        when(refreshTokenRedisRepository.findById(1L)).thenReturn(Optional.of(refreshToken));

        doNothing().when(jwtProvider).addAccessToken(any(Authentication.class), any(HttpServletResponse.class));
        doNothing().when(jwtProvider).addRefreshToken(any(Authentication.class), any(HttpServletResponse.class));

        authService.reissue();

        verify(jwtProvider, times(1)).addAccessToken(authentication, response);
        verify(jwtProvider, times(1)).addRefreshToken(authentication, response);
    }

    @Test
    void reissue_Failure_NoRefreshToken() {
        mockCookieUtil.when(() -> CookieUtil.getCookie(request, "refresh")).thenReturn(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> authService.reissue());
        assertEquals(FailureInfo.REFRESH_TOKEN_NULL, exception.getFailureInfo());
    }

    @Test
    void reissue_Failure_InvalidRefreshToken() {
        String refreshTokenValue = "invalidRefreshToken";
        Cookie refreshCookie = new Cookie("refresh", refreshTokenValue);

        mockCookieUtil.when(() -> CookieUtil.getCookie(request, "refresh")).thenReturn(refreshCookie);

        when(jwtProvider.validateToken(refreshTokenValue)).thenReturn(false);

        GlobalException exception = assertThrows(GlobalException.class, () -> authService.reissue());
        assertEquals(FailureInfo.INVALID_REFRESH_TOKEN, exception.getFailureInfo());
    }

    @Test
    void reissue_Failure_TokenNotFoundInRepository() {
        String refreshTokenValue = "validRefreshToken";
        Cookie refreshCookie = new Cookie("refresh", refreshTokenValue);

        mockCookieUtil.when(() -> CookieUtil.getCookie(request, "refresh")).thenReturn(refreshCookie);

        when(jwtProvider.validateToken(refreshTokenValue)).thenReturn(true);

        User mockUser = User.builder().id(1L).build();
        CustomUserDetails customUserDetails = new CustomUserDetails(mockUser);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(jwtProvider.getAuthentication(refreshTokenValue)).thenReturn(authentication);

        when(refreshTokenRedisRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> authService.reissue());
        assertEquals(FailureInfo.INVALID_REFRESH_TOKEN, exception.getFailureInfo());
    }

    @Test
    void reissue_Failure_TokenMismatch() {
        String refreshTokenValue = "validRefreshToken";
        String storedRefreshTokenValue = "differentRefreshToken";
        Cookie refreshCookie = new Cookie("refresh", refreshTokenValue);

        mockCookieUtil.when(() -> CookieUtil.getCookie(request, "refresh")).thenReturn(refreshCookie);

        when(jwtProvider.validateToken(refreshTokenValue)).thenReturn(true);

        User mockUser = User.builder().id(1L).build();
        CustomUserDetails customUserDetails = new CustomUserDetails(mockUser);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(jwtProvider.getAuthentication(refreshTokenValue)).thenReturn(authentication);

        RefreshToken storedRefreshToken = RefreshToken.builder()
                .id(1L)
                .token(storedRefreshTokenValue)
                .build();
        when(refreshTokenRedisRepository.findById(1L)).thenReturn(Optional.of(storedRefreshToken));

        GlobalException exception = assertThrows(GlobalException.class, () -> authService.reissue());
        assertEquals(FailureInfo.INVALID_REFRESH_TOKEN, exception.getFailureInfo());
    }

    @Test
    void checkEmailDuplication_EmailAlreadyExists() {
        String email = "test@gmail.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        GlobalException exception = assertThrows(GlobalException.class, () -> authService.checkEmailDuplication(email));
        assertEquals(FailureInfo.ALREADY_EXIST_EMAIL, exception.getFailureInfo());

        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void checkEmailDuplication_EmailNotExists() {
        String email = "test@gmail.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertDoesNotThrow(() -> authService.checkEmailDuplication(email));

        verify(userRepository, times(1)).existsByEmail(email);
    }
}
