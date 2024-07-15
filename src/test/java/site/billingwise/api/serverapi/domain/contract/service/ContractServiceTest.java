package site.billingwise.api.serverapi.domain.contract.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import site.billingwise.api.serverapi.domain.consent.repository.ConsentAccountRepository;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.contract.dto.request.CreateContractDto;
import site.billingwise.api.serverapi.domain.contract.dto.request.EditContractDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.CreateBulkContractResultDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.GetContractAllDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.GetContractDto;
import site.billingwise.api.serverapi.domain.contract.repository.ContractRepository;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.service.ItemService;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.service.MemberService;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.EnumUtil;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ContractServiceTest {
    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ConsentAccountRepository consentAccountRepository;

    @Mock
    private Validator validator;

    @Mock
    private ItemService itemService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private ContractService contractService;

    MockedStatic<SecurityUtil> mockSecurityUtil;

    private Client mockClient;
    private User mockUser;
    private Item mockItem;
    private Member mockMember;
    private Contract mockContract;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockSecurityUtil = mockStatic(SecurityUtil.class);

        mockClient = Client.builder().id(1L).build();
        mockUser = User.builder().client(mockClient).build();

        mockMember = Member.builder()
                .id(1L)
                .client(mockClient)
                .name("kim")
                .email("example@example.com")
                .phone("010-1234-5678")
                .description("Test description")
                .contractList(new HashSet<>())
                .build();

        mockItem = Item.builder()
                .id(1L)
                .name("Old Name")
                .price(1000L)
                .description("Old Description")
                .imageUrl("test.png")
                .client(mockClient)
                .build();

        mockContract = Contract.builder()
                .id(1L)
                .member(mockMember)
                .item(mockItem)
                .invoiceType(InvoiceType.AUTO)
                .paymentType(PaymentType.AUTO_TRANSFER)
                .contractStatus(ContractStatus.PROGRESS)
                .isSubscription(true)
                .itemPrice(1000L)
                .itemAmount(2)
                .contractCycle(10)
                .paymentDueCycle(10)
                .isEasyConsent(true)
                .invoiceList(new HashSet<>())
                .build();

    }

    @AfterEach
    void tearDown() {
        mockSecurityUtil.close();
    }

    @Test
    void createContract() {
        // given
        CreateContractDto createContractDto = CreateContractDto.builder()
                .memberId(1L)
                .itemId(1L)
                .itemPrice(1000L)
                .itemAmount(2)
                .isSubscription(true)
                .invoiceTypeId(1L)
                .paymentTypeId(2L)
                .isEasyConsent(true)
                .contractCycle(10)
                .paymentDueCycle(10)
                .build();

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(itemService.getEntity(any(Client.class), anyLong())).thenReturn(mockItem);
        when(memberService.getEntity(any(Client.class), anyLong())).thenReturn(mockMember);
        when(consentAccountRepository.existsById(anyLong())).thenReturn(false);

        // when
        contractService.createContract(createContractDto);

        // then
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    void createContractInvalidInput() {
        // given
        CreateContractDto createContractDto = CreateContractDto.builder()
                .memberId(1L)
                .itemId(1L)
                .itemPrice(1000L)
                .itemAmount(2)
                .isSubscription(true)
                .invoiceTypeId(1L)
                .paymentTypeId(1L)
                .isEasyConsent(true)
                .contractCycle(15)
                .paymentDueCycle(10)
                .build();

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(itemService.getEntity(any(Client.class), anyLong())).thenReturn(mockItem);
        when(memberService.getEntity(any(Client.class), anyLong())).thenReturn(mockMember);
        when(consentAccountRepository.existsById(anyLong())).thenReturn(false);

        // when
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            contractService.createContract(createContractDto);
        });

        // then
        assertEquals(FailureInfo.INVALID_DUE_CYCLE, exception.getFailureInfo());
    }

    @Test
    void editContract() {
        // given
        EditContractDto editContractDto = EditContractDto.builder()
                .itemPrice(1000L)
                .itemAmount(2)
                .invoiceTypeId(1L)
                .paymentTypeId(1L)
                .isEasyConsent(true)
                .contractCycle(10)
                .paymentDueCycle(10)
                .build();

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(contractRepository.findById(anyLong())).thenReturn(Optional.of(mockContract));
        when(consentAccountRepository.existsById(anyLong())).thenReturn(false);

        // when
        contractService.editContract(1L, editContractDto);

        // then
        assertEquals(editContractDto.getItemPrice(), mockContract.getItemPrice());
        assertEquals(editContractDto.getItemAmount(), mockContract.getItemAmount());
        assertEquals(EnumUtil.toEnum(InvoiceType.class, 1L), mockContract.getInvoiceType());
        assertEquals(EnumUtil.toEnum(PaymentType.class, 1L), mockContract.getPaymentType());
        assertEquals(editContractDto.getIsEasyConsent(), mockContract.getIsEasyConsent());
        assertEquals(editContractDto.getContractCycle(), mockContract.getContractCycle());
        assertEquals(editContractDto.getPaymentDueCycle(), mockContract.getPaymentDueCycle());
    }

    @Test
    void editContractNotExist() {
        // given
        EditContractDto editContractDto = EditContractDto.builder()
                .itemPrice(1000L)
                .itemAmount(2)
                .invoiceTypeId(1L)
                .paymentTypeId(1L)
                .isEasyConsent(true)
                .contractCycle(10)
                .paymentDueCycle(10)
                .build();

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(contractRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            contractService.editContract(1L, editContractDto);
        });

        // then
        assertEquals(FailureInfo.NOT_EXIST_CONTRACT, exception.getFailureInfo());
    }

    @Test
    void deleteContract() {
        // given
        Long contractId = 1L;

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(contractRepository.findById(anyLong())).thenReturn(Optional.of(mockContract));

        // when
        contractService.deleteContract(contractId);

        // then
        verify(contractRepository, times(1)).delete(mockContract);
    }

    @Test
    void deleteContractNotExist() {
        // given
        Long contractId = 1L;

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(contractRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            contractService.deleteContract(contractId);
        });

        // then
        assertEquals(FailureInfo.NOT_EXIST_CONTRACT, exception.getFailureInfo());
    }

    @Test
    void getContract() throws Exception {
        // given
        Long contractId = 1L;

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(contractRepository.findWithItemWithMemberById(contractId)).thenReturn(Optional.of(mockContract));

        // when
        GetContractDto getContractDto = contractService.getContract(contractId);

        // then
        assertEquals(mockContract.getItem().getName(), getContractDto.getItem().getName());
        assertEquals(mockContract.getMember().getName(), getContractDto.getMember().getName());
    }

    @Test
    void getContractNotExist() {
        // given
        Long contractId = 1L;

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(contractRepository.findWithItemWithMemberById(anyLong())).thenReturn(Optional.empty());

        // when
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            contractService.getContract(contractId);
        });

        // then
        assertEquals(FailureInfo.NOT_EXIST_CONTRACT, exception.getFailureInfo());
    }

    @Test
    public void getContractList() {
        // given
        String itemName = "item";
        String memberName = "member";
        Boolean isSubscription = true;
        Long invoiceTypeId = 1L;
        Long contractStatusId = 1L;
        Long paymentStatusId = 2L;
        Pageable pageable = Pageable.ofSize(10);

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        List<Contract> contractList = new ArrayList<>();
        contractList.add(mockContract);

        Page<Contract> contractPage = new PageImpl<>(contractList, PageRequest.of(0, 10), contractList.size());

        when(contractRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(contractPage);

        // when
        Page<GetContractAllDto> result = contractService.getContractList(
                itemName, memberName, isSubscription, invoiceTypeId, contractStatusId, paymentStatusId, pageable);

        // then
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void createContractBulk_Success() throws Exception {
        // given
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(itemService.getEntity(any(Client.class), any(Long.class))).thenReturn(mockItem);
        when(memberService.getEntity(any(Client.class), any(Long.class))).thenReturn(mockMember);
        when(contractRepository.save(any(Contract.class))).thenReturn(mockContract);

        doNothing().when(validator).validate(any(), any(BindingResult.class));
        
        InputStream inputStream = getClass().getResourceAsStream("/exel/contract_test_success.xlsx");
        MockMultipartFile file = new MockMultipartFile("file", "contract_test_success.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", inputStream);

        // when
        CreateBulkContractResultDto resultDto = contractService.createContractBulk(file);

        // Assertions
        assertTrue(resultDto.isSuccess());
        assertFalse(resultDto.getErrorList().contains("error")); // Adjust based on expected errors
        assertEquals(3, resultDto.getContractList().size()); // Adjust based on expected number of contracts created
    }

    @Test
    public void createContractBulkInvalidFile() {
        // given
        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));

        MockMultipartFile mockFile = new MockMultipartFile("file", "invalid.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new byte[0]);

        // when, then
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            contractService.createContractBulk(mockFile);
        });

        assertEquals(FailureInfo.INVALID_FILE, exception.getFailureInfo());
    }
}
