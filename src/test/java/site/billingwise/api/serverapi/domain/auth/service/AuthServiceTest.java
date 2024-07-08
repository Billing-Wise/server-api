package site.billingwise.api.serverapi.domain.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.billingwise.api.serverapi.domain.auth.dto.RegisterDto;
import site.billingwise.api.serverapi.domain.auth.service.AuthService;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.domain.user.repository.UserRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
}