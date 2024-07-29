package site.billingwise.api.serverapi.domain.consent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import site.billingwise.api.serverapi.domain.consent.ConsentAccount;
import site.billingwise.api.serverapi.domain.consent.dto.request.ConsentWithNonMemberDto;
import site.billingwise.api.serverapi.domain.consent.dto.request.RegisterConsentDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetBasicItemDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetContractInfoDto;
import site.billingwise.api.serverapi.domain.consent.repository.ConsentAccountRepository;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.contract.repository.ContractRepository;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.repository.MemberRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class EasyConsentServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ConsentAccountRepository consentAccountRepository;

    @Mock
    private ConsentService consentService;

    @InjectMocks
    private EasyConsentService easyConsentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBasicItemList() {
        Long clientId = 1L;

        // Given
        List<Item> items = Arrays.asList(
                Item.builder()
                        .id(1L)
                        .name("test1")
                        .price(1000L)
                        .imageUrl("test1.png")
                        .build(),
                Item.builder()
                        .id(2L)
                        .name("test2")
                        .price(2000L)
                        .imageUrl("test2.png")
                        .build()
        );
        when(itemRepository.findAllByClientIdAndIsBasic(clientId, true)).thenReturn(items);

        // When
        List<GetBasicItemDto> result = easyConsentService.getBasicItemList(clientId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("test1");
        assertThat(result.get(0).getPrice()).isEqualTo(1000);
        assertThat(result.get(0).getImageUrl()).isEqualTo("test1.png");

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("test2");
        assertThat(result.get(1).getPrice()).isEqualTo(2000);
        assertThat(result.get(1).getImageUrl()).isEqualTo("test2.png");
    }

    @Test
    void testGetContractInfoSuccess() {
        Long contractId = 1L;

        // Given
        Contract contract = Contract.builder()
                .id(contractId)
                .paymentType(PaymentType.AUTO_TRANSFER)
                .isEasyConsent(true)
                .contractStatus(ContractStatus.PENDING)
                .member(Member.builder().id(1L).name("홍길동").build())
                .item(Item.builder().id(1L).name("Item1").price(1000L).build())
                .itemPrice(1000L)
                .itemAmount(3)
                .build();

        when(contractRepository.findWithItemWithMemberById(contractId)).thenReturn(Optional.of(contract));

        // When
        GetContractInfoDto result = easyConsentService.getContractInfo(contractId);

        // Then
        assertThat(result.getContractId()).isEqualTo(contractId);
        assertThat(result.getMemberName()).isEqualTo("홍길동");
        assertThat(result.getItemName()).isEqualTo("Item1");
    }

    @Test
    void testGetContractInfoNotExist() {
        Long contractId = 1L;

        // Given
        when(contractRepository.findWithItemWithMemberById(contractId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> easyConsentService.getContractInfo(contractId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(FailureInfo.NOT_EXIST_CONTRACT.getMessage());
    }

    @Test
    void testGetContractInfoNotAutoTransfer() {
        Long contractId = 1L;

        // Given
        Contract contract = Contract.builder()
                .id(contractId)
                .paymentType(PaymentType.PAYER_PAYMENT)
                .isEasyConsent(true)
                .contractStatus(ContractStatus.PENDING)
                .build();

        when(contractRepository.findWithItemWithMemberById(contractId)).thenReturn(Optional.of(contract));

        // Then
        assertThatThrownBy(() -> easyConsentService.getContractInfo(contractId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(FailureInfo.NOT_CMS.getMessage());
    }

    @Test
    void testGetContractInfoNotEasyConsent() {
        Long contractId = 1L;

        // Given
        Contract contract = Contract.builder()
                .id(contractId)
                .paymentType(PaymentType.AUTO_TRANSFER)
                .isEasyConsent(false)
                .contractStatus(ContractStatus.PENDING)
                .build();

        when(contractRepository.findWithItemWithMemberById(contractId)).thenReturn(Optional.of(contract));

        // Then
        assertThatThrownBy(() -> easyConsentService.getContractInfo(contractId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(FailureInfo.NOT_EASY_CONSENT_CONTRACT.getMessage());
    }

    @Test
    void testGetContractInfoNotPending() {
        Long contractId = 1L;

        // Given
        Contract contract = Contract.builder()
                .id(contractId)
                .paymentType(PaymentType.AUTO_TRANSFER)
                .isEasyConsent(true)
                .contractStatus(ContractStatus.PROGRESS)
                .build();

        when(contractRepository.findWithItemWithMemberById(contractId)).thenReturn(Optional.of(contract));

        // Then
        assertThatThrownBy(() -> easyConsentService.getContractInfo(contractId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(FailureInfo.NOT_PENDING_CONTRACT.getMessage());
    }

    @Test
    void testConsentWithNonMemberSuccess() throws Exception {
        // given
        Long clientId = 1L;
        ConsentWithNonMemberDto consentWithNonMemberDto = ConsentWithNonMemberDto.builder()
                .memberName("홍길동")
                .memberEmail("test@gmail.com")
                .memberPhone("01012341234")
                .itemId(1L)
                .itemAmount(3)
                .isSubscription(true)
                .contractCycle(15)
                .accountBank("은행")
                .accountOwner("홍길동")
                .accountNumber("1234567890")
                .build();

        MockMultipartFile signImage = new MockMultipartFile(
                "signImage", "sign.png", "image/png", "consent data".getBytes());

        Client client = Client.builder().id(clientId).build();
        Item item = Item.builder()
                .id(consentWithNonMemberDto.getItemId())
                .client(client)
                .price(1000L)
                .isBasic(true)
                .build();
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(itemRepository.findByIdAndClientIdAndIsBasic(consentWithNonMemberDto.getItemId(), clientId, true))
                .thenReturn(Optional.of(item));
        when(memberRepository.existsByClientIdAndEmail(clientId, consentWithNonMemberDto.getMemberEmail()))
                .thenReturn(false);
        when(consentService.uploadImage(signImage)).thenReturn("sign-url");

        // when
        easyConsentService.consentForNonMember(clientId, consentWithNonMemberDto, signImage);

        // then
        verify(memberRepository).save(any(Member.class));
        verify(consentAccountRepository).save(any(ConsentAccount.class));
        verify(contractRepository).save(any(Contract.class));
        assertEquals("회원 이메일이 일치해야 합니다.", consentWithNonMemberDto.getMemberEmail(), "test@gmail.com");
        assertEquals("상품 아이디가 일치해야 합니다.", consentWithNonMemberDto.getItemId(), 1L);
    }

    @Test
    void testConsentForMemberSuccess() throws Exception {
        // given
        Long contractId = 1L;
        RegisterConsentDto registerConsentDto = RegisterConsentDto.builder()
                .owner("홍길동")
                .bank("은행")
                .number("1234567890")
                .build();

        MockMultipartFile signImage = new MockMultipartFile(
                "signImage", "sign.png", "image/png", "consent data".getBytes());

        Member member = Member.builder().id(1L).name("홍길동").contractList(new HashSet<Contract>()).build();
        Contract contract = Contract.builder()
                .id(contractId)
                .paymentType(PaymentType.AUTO_TRANSFER)
                .isEasyConsent(true)
                .contractStatus(ContractStatus.PENDING)
                .member(member)
                .build();

        member.getContractList().add(contract);

        when(contractRepository.findWithMemberById(contractId)).thenReturn(Optional.of(contract));
        when(consentService.uploadImage(signImage)).thenReturn("sign-url");
        when(consentAccountRepository.existsById(member.getId())).thenReturn(false);

        // when
        easyConsentService.consentForMember(contractId, registerConsentDto, signImage);

        // then
        verify(consentAccountRepository).save(any(ConsentAccount.class));
    }
}
