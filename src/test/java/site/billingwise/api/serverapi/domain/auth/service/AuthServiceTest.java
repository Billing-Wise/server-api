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
    private EmailCodeRedisRepository emailCodeRedisRepository;

    @Mock
    private PhoneCodeRedisRepository phoneCodeRedisRepository;

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

    @Test
    void authenticateEmail_Success() {
        String email = "test@example.com";
        Integer validCode = 123456;


        EmailCode storedEmailCode = EmailCode.builder()
                .email(email)
                .code(validCode)
                .build();

        when(emailCodeRedisRepository.findById(email)).thenReturn(Optional.of(storedEmailCode));

        assertDoesNotThrow(() -> authService.authenticateEmail(email, validCode));

        verify(emailCodeRedisRepository, times(1)).findById(email);
    }

    @Test
    void authenticateEmail_InvalidCode() {
        String email = "test@example.com";
        Integer invalidCode = 654321;

        EmailCode storedEmailCode = EmailCode.builder()
                .email(email)
                .code(123456)
                .build();

        when(emailCodeRedisRepository.findById(email)).thenReturn(Optional.of(storedEmailCode));

        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.authenticateEmail(email, invalidCode));
        verify(emailCodeRedisRepository, times(1)).findById(email);
        verifyNoMoreInteractions(emailCodeRedisRepository);
        assertEquals(FailureInfo.INVALID_MAIL_CODE, exception.getFailureInfo());
    }

    @Test
    void authenticateEmail_CodeNotFound() {
        String email = "test@example.com";
        Integer validCode = 123456;

        when(emailCodeRedisRepository.findById(anyString())).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.authenticateEmail(email, validCode));
        verify(emailCodeRedisRepository, times(1)).findById(email);
        verifyNoMoreInteractions(emailCodeRedisRepository);
        assertEquals(FailureInfo.INVALID_MAIL_CODE, exception.getFailureInfo());
    }

    @Test
    void authenticatePhone_Success() {
        String validPhone = "01012341234";
        Integer validCode = 123456;

        PhoneCode phoneCode = PhoneCode.builder()
                .phone(validPhone)
                .code(validCode)
                .build();

        when(phoneCodeRedisRepository.findById(validPhone)).thenReturn(Optional.of(phoneCode));

        assertDoesNotThrow(() -> authService.authenticatePhone(validPhone, validCode));
        verify(phoneCodeRedisRepository, times(1)).findById(validPhone);
    }

    @Test
    void authenticatePhone_PhoneNotFound() {

        String inValidPhone = "01012341234";
        Integer validCode = 123456;

        // Arrange
        when(phoneCodeRedisRepository.findById(inValidPhone)).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.authenticatePhone(inValidPhone, validCode));
        assertEquals(FailureInfo.INVALID_PHONE_CODE, exception.getFailureInfo());
        verify(phoneCodeRedisRepository, times(1)).findById(inValidPhone);
    }

    @Test
    void authenticatePhone_CodeMismatch() {

        String validPhone = "01012341234";
        Integer inValidCode = 123456;

        PhoneCode phoneCode = PhoneCode.builder()
                .phone(validPhone)
                .code(inValidCode)
                .build();

        // Arrange
        when(phoneCodeRedisRepository.findById(validPhone)).thenReturn(Optional.of(phoneCode));
        Integer invalidCode = 5678;

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.authenticatePhone(validPhone, invalidCode));
        assertEquals(FailureInfo.INVALID_PHONE_CODE, exception.getFailureInfo());
        verify(phoneCodeRedisRepository, times(1)).findById(validPhone);
    }

    @Test
    void findEmail_Success() {
        String name = "홍길동";
        String phone = "01012345678";
        Integer code = 123456;
        String email = "test@gmail.com";

        FindEmailDto findEmailDto = FindEmailDto.builder()
                .name(name)
                .phone(phone)
                .code(code)
                .build();

        PhoneCode phoneCode = PhoneCode.builder()
                .phone(phone)
                .code(code)
                .build();

        User user = User.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .build();

        when(phoneCodeRedisRepository.findById(phone)).thenReturn(Optional.of(phoneCode));
        when(userRepository.findByNameAndPhone(name, phone)).thenReturn(Optional.of(user));

        EmailDto emailDto = authService.findEmail(findEmailDto);

        assertNotNull(emailDto);
        assertEquals(email, emailDto.getEmail());

        verify(phoneCodeRedisRepository, times(1)).findById(phone);
        verify(userRepository, times(1)).findByNameAndPhone(name, phone);
    }

    @Test
    void findEmail_UserNotFound() {
        String name = "홍길동";
        String phone = "01012345678";
        Integer code = 123456;

        FindEmailDto findEmailDto = FindEmailDto.builder()
                .name(name)
                .phone(phone)
                .code(code)
                .build();

        PhoneCode phoneCode = PhoneCode.builder()
                .phone(phone)
                .code(code)
                .build();

        when(phoneCodeRedisRepository.findById(phone)).thenReturn(Optional.of(phoneCode));
        when(userRepository.findByNameAndPhone(name, phone)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.findEmail(findEmailDto));
        assertEquals(FailureInfo.NOT_EXIST_USER, exception.getFailureInfo());

        verify(phoneCodeRedisRepository, times(1)).findById(phone);
        verify(userRepository, times(1)).findByNameAndPhone(name, phone);
    }

    @Test
    void findEmail_InvalidPhoneCode() {
        String name = "홍길동";
        String phone = "010-1234-5678";
        Integer code = 123456;

        FindEmailDto findEmailDto = FindEmailDto.builder()
                .name(name)
                .phone(phone)
                .code(code)
                .build();

        when(phoneCodeRedisRepository.findById(phone)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.findEmail(findEmailDto));
        assertEquals(FailureInfo.INVALID_PHONE_CODE, exception.getFailureInfo());

        verify(phoneCodeRedisRepository, times(1)).findById(phone);
        verify(userRepository, never()).findByNameAndPhone(anyString(), anyString());
    }

    @Test
    void findEmail_PhoneCodeMismatch() {
        String name = "홍길동";
        String phone = "010-1234-5678";
        Integer code = 123456;
        Integer wrongCode = 654321;

        FindEmailDto findEmailDto = FindEmailDto.builder()
                .name(name)
                .phone(phone)
                .code(wrongCode)
                .build();

        PhoneCode phoneCode = PhoneCode.builder()
                .phone(phone)
                .code(code)
                .build();

        when(phoneCodeRedisRepository.findById(phone)).thenReturn(Optional.of(phoneCode));

        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.findEmail(findEmailDto));
        assertEquals(FailureInfo.INVALID_PHONE_CODE, exception.getFailureInfo());

        verify(phoneCodeRedisRepository, times(1)).findById(phone);
        verify(userRepository, never()).findByNameAndPhone(anyString(), anyString());
    }

    @Test
    void findPassword_Success() {
        FindPasswordDto findPasswordDto = FindPasswordDto.builder()
                .email("test@gmail.com")
                .code(123123)
                .newPassword("test1234!")
                .newPasswordCheck("test1234!")
                .build();

        User user = User.builder()
                .email("test@gmail.com")
                .password("oldPassword")
                .build();

        EmailCode emailCode = EmailCode.builder()
                .email("test@gmail.com")
                .code(123123)
                .build();

        Optional<User> optionalUser = Optional.of(user);

        when(userRepository.findByEmail(findPasswordDto.getEmail())).thenReturn(optionalUser);
        when(emailCodeRedisRepository.findById(findPasswordDto.getEmail()))
                .thenReturn(Optional.of(emailCode));
        when(passwordEncoder.encode(findPasswordDto.getNewPassword())).thenReturn("test1234!");

        authService.findPassword(findPasswordDto);

        assertEquals(findPasswordDto.getNewPassword(), user.getPassword());

    }

    @Test
    void testFindPassword_WrongCode() {
        FindPasswordDto findPasswordDto = FindPasswordDto.builder()
                .email("test@gmail.com")
                .code(123123)
                .newPassword("test1234!")
                .newPasswordCheck("test1234!")
                .build();

        EmailCode emailCode = EmailCode.builder()
                .email("test@gmail.com")
                .code(321321)
                .build();

        when(emailCodeRedisRepository.findById(findPasswordDto.getEmail())).thenReturn(Optional.of(emailCode));

        assertThrows(GlobalException.class, () -> authService.findPassword(findPasswordDto),
                FailureInfo.INVALID_MAIL_CODE.getMessage());
    }

    @Test
    void testFindPassword_PasswordMismatch() {
        FindPasswordDto findPasswordDto = FindPasswordDto.builder()
                .email("test@gmail.com")
                .code(123123)
                .newPassword("test1234!")
                .newPasswordCheck("wrongtest1234!")
                .build();

        EmailCode emailCode = EmailCode.builder()
                .email("test@gmail.com")
                .code(123123)
                .build();

        when(emailCodeRedisRepository.findById(findPasswordDto.getEmail())).thenReturn(Optional.of(emailCode));

        assertThrows(GlobalException.class, () -> authService.findPassword(findPasswordDto),
                FailureInfo.NEW_PASSWORD_MISMATCH.getMessage());
    }

    @Test
    void testFindPassword_UserNotFound() {
        FindPasswordDto findPasswordDto = FindPasswordDto.builder()
                .email("test@gmail.com")
                .code(123123)
                .newPassword("test1234!")
                .newPasswordCheck("test1234!")
                .build();

        // Simulate user not found scenario
        when(userRepository.findByEmail(findPasswordDto.getEmail())).thenReturn(Optional.empty());

        // Execute and assert the exception
        assertThrows(GlobalException.class, () -> authService.findPassword(findPasswordDto),
                FailureInfo.NOT_EXIST_USER.getMessage());
    }
}
