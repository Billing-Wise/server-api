package site.billingwise.api.serverapi.domain.consent.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import site.billingwise.api.serverapi.domain.consent.ConsentAccount;
import site.billingwise.api.serverapi.domain.consent.dto.request.RegisterConsentDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetConsentDto;
import site.billingwise.api.serverapi.domain.consent.repository.ConsentAccountRepository;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.dto.request.CreateItemDto;
import site.billingwise.api.serverapi.domain.item.service.ItemService;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.repository.MemberRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.service.S3Service;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ConsentServiceTest {

    @Mock
    private ConsentAccountRepository consentAccountRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ConsentService consentService;

    private MockedStatic<SecurityUtil> mockSecurityUtil;

    private Client mockClient;

    private static final String signImageDirectory = "sign";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(consentService, "signImageDirectory", signImageDirectory);

        mockSecurityUtil = mockStatic(SecurityUtil.class);

        mockClient = Client.builder()
                .id(1L)
                .build();
    }

    @AfterEach
    void tearDown() {
        mockSecurityUtil.close();
    }

    @Test
    void registerConsent() {
        // given
        RegisterConsentDto registerConsentDto = RegisterConsentDto.builder()
                .owner("홍길동")
                .bank("신한")
                .number("111222333444")
                .build();

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());


        Member member = Member.builder().client(mockClient).build();

        ConsentAccount consentAccount = registerConsentDto.toEntity(member, " ");

        when(SecurityUtil.getCurrentClient()).thenReturn(mockClient);

        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        when(s3Service.upload(eq(multipartFile), eq(signImageDirectory))).thenReturn("s3://bucket/test.jpg");

        // when
        consentService.registerConsent(1L, registerConsentDto, multipartFile);

        // then
        verify(consentAccountRepository, times(1)).save(any(ConsentAccount.class));
        verify(s3Service, times(1)).upload(any(), any());
    }

    @Test
    void getConsent_Success() {
        // given
        Long memberId = 1L;
        Member member = Member.builder().client(mockClient).build();
        ConsentAccount consentAccount = ConsentAccount.builder().id(memberId).build();

        when(SecurityUtil.getCurrentClient()).thenReturn(mockClient);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(consentAccountRepository.findById(memberId)).thenReturn(Optional.of(consentAccount));

        // when
        GetConsentDto getConsentDto = consentService.getConsent(memberId);

        // then
        assertNotNull(getConsentDto);
        verify(memberRepository, times(1)).findById(memberId);
        verify(consentAccountRepository, times(1)).findById(memberId);
    }

    @Test
    void getConsent_MemberNotFound() {
        // given
        Long memberId = 1L;

        when(SecurityUtil.getCurrentClient()).thenReturn(mockClient);
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when / then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            consentService.getConsent(memberId);
        });

        assertEquals(FailureInfo.NOT_EXIST_MEMBER, exception.getFailureInfo());
        verify(memberRepository, times(1)).findById(memberId);
        verify(consentAccountRepository, times(0)).findById(any());
    }

    @Test
    void getConsent_ConsentNotFound() {
        // given
        Long memberId = 1L;
        Member member = Member.builder().client(mockClient).build();

        when(SecurityUtil.getCurrentClient()).thenReturn(mockClient);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(consentAccountRepository.findById(memberId)).thenReturn(Optional.empty());

        // when / then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            consentService.getConsent(memberId);
        });

        assertEquals(FailureInfo.NOT_EXIST_CONSENT, exception.getFailureInfo());
        verify(memberRepository, times(1)).findById(memberId);
        verify(consentAccountRepository, times(1)).findById(memberId);
    }

    @Test
    void getConsent_AccessDenied() {
        // given
        Long memberId = 1L;
        Client anotherClient = Client.builder().id(2L).build();
        Member member = Member.builder().client(anotherClient).build();

        when(SecurityUtil.getCurrentClient()).thenReturn(mockClient);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when / then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            consentService.getConsent(memberId);
        });

        assertEquals(FailureInfo.ACCESS_DENIED, exception.getFailureInfo());
        verify(memberRepository, times(1)).findById(memberId);
        verify(consentAccountRepository, times(0)).findById(any());
    }

    @Test
    void editConsent_Success() {
        // given
        Long memberId = 1L;
        RegisterConsentDto editConsentDto = RegisterConsentDto.builder()
                .owner("홍길동")
                .bank("국민")
                .number("555666777888")
                .build();
        Member member = Member.builder().client(mockClient).build();
        ConsentAccount consentAccount = ConsentAccount.builder().id(memberId).build();

        when(SecurityUtil.getCurrentClient()).thenReturn(mockClient);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(consentAccountRepository.findById(memberId)).thenReturn(Optional.of(consentAccount));

        // when
        consentService.editConsent(memberId, editConsentDto);

        // then
        verify(consentAccountRepository, times(1)).findById(memberId);
        assertEquals("홍길동", consentAccount.getOwner());
        assertEquals("국민", consentAccount.getBank());
        assertEquals("555666777888", consentAccount.getNumber());
    }

    @Test
    void editConsent_MemberNotFound() {
        // given
        Long memberId = 1L;
        RegisterConsentDto editConsentDto = RegisterConsentDto.builder()
                .owner("홍길동")
                .bank("국민")
                .number("555666777888")
                .build();

        when(SecurityUtil.getCurrentClient()).thenReturn(mockClient);
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // when / then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            consentService.editConsent(memberId, editConsentDto);
        });

        assertEquals(FailureInfo.NOT_EXIST_MEMBER, exception.getFailureInfo());
        verify(memberRepository, times(1)).findById(memberId);
        verify(consentAccountRepository, times(0)).findById(any());
    }

    @Test
    void editConsent_ConsentNotFound() {
        // given
        Long memberId = 1L;
        RegisterConsentDto editConsentDto = RegisterConsentDto.builder()
                .owner("홍길동")
                .bank("국민")
                .number("555666777888")
                .build();
        Member member = Member.builder().client(mockClient).build();

        when(SecurityUtil.getCurrentClient()).thenReturn(mockClient);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(consentAccountRepository.findById(memberId)).thenReturn(Optional.empty());

        // when / then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            consentService.editConsent(memberId, editConsentDto);
        });

        assertEquals(FailureInfo.NOT_EXIST_CONSENT, exception.getFailureInfo());
        verify(memberRepository, times(1)).findById(memberId);
        verify(consentAccountRepository, times(1)).findById(memberId);
    }

    @Test
    void editConsent_AccessDenied() {
        // given
        Long memberId = 1L;
        Client anotherClient = Client.builder().id(2L).build();
        RegisterConsentDto editConsentDto = RegisterConsentDto.builder()
                .owner("홍길동")
                .bank("국민")
                .number("555666777888")
                .build();
        Member member = Member.builder().client(anotherClient).build();

        when(SecurityUtil.getCurrentClient()).thenReturn(mockClient);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when / then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            consentService.editConsent(memberId, editConsentDto);
        });

        assertEquals(FailureInfo.ACCESS_DENIED, exception.getFailureInfo());
        verify(memberRepository, times(1)).findById(memberId);
        verify(consentAccountRepository, times(0)).findById(any());
    }
}
