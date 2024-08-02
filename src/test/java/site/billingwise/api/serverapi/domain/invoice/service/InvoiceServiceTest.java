package site.billingwise.api.serverapi.domain.invoice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.contract.service.ContractService;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;
import site.billingwise.api.serverapi.domain.invoice.dto.request.CreateInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.request.EditInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.GetInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.GetInvoiceListDto;
import site.billingwise.api.serverapi.domain.invoice.repository.InvoiceRepository;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.util.EnumUtil;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

public class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private ContractService contractService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PaymentRepository paymentRepository;

    MockedStatic<SecurityUtil> mockSecurityUtil;

    private Client mockClient;
    private User mockUser;
    private Item mockItem;
    private Member mockMember;
    private Contract mockContract;
    private Invoice mockInvoice;

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
                .paymentType(PaymentType.REALTIME_CMS)
                .contractStatus(ContractStatus.PROGRESS)
                .isSubscription(true)
                .itemPrice(1000L)
                .itemAmount(2)
                .contractCycle(10)
                .paymentDueCycle(10)
                .isEasyConsent(true)
                .invoiceList(new HashSet<>())
                .build();

        mockInvoice = Invoice.builder()
                        .id(1L)
                        .contract(mockContract)
                        .invoiceType(InvoiceType.AUTO)
                        .paymentType(PaymentType.REALTIME_CMS)
                        .paymentStatus(PaymentStatus.PENDING)
                        .chargeAmount(10000L)
                        .contractDate(LocalDateTime.now().plusDays(5))
                        .dueDate(LocalDateTime.now().plusDays(10))
                        .build();
    
    }

    @AfterEach
    void tearDown() {
        mockSecurityUtil.close();
    }


    @Test
    void createInvoice() {
        // given
        CreateInvoiceDto dto = CreateInvoiceDto.builder()
                .contractId(1L)
                .paymentTypeId(1L)
                .chargeAmount(1000L)
                .contractDate(LocalDate.now().plusDays(1))
                .dueDate(LocalDate.now().plusDays(30))
                .build();

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(contractService.getEntity(any(), any())).thenReturn(mockContract);
        when(invoiceRepository.existByMonthlyInvoice(any(), any(), any())).thenReturn(false);

        // when
        invoiceService.createInvoice(dto);

        // given
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void editInvoice() {
        // given
        Long invoiceId = 1L;

        EditInvoiceDto dto = EditInvoiceDto.builder()
                .paymentTypeId(1L)
                .chargeAmount(2000L)
                .contractDate(LocalDate.now().plusDays(5))
                .dueDate(LocalDate.now().plusDays(30))
                .build();

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(invoiceRepository.findById(any())).thenReturn(Optional.of(mockInvoice));
        when(paymentRepository.existsById(invoiceId)).thenReturn(false);

        // when
        invoiceService.editInvoice(invoiceId, dto);

        // then
        assertEquals(mockInvoice.getChargeAmount(), dto.getChargeAmount());
        assertEquals(mockInvoice.getContractDate(), dto.getContractDate().atStartOfDay());
        assertEquals(mockInvoice.getDueDate(), dto.getDueDate().atStartOfDay());
        assertEquals(mockInvoice.getPaymentType(), EnumUtil.toEnum(PaymentType.class, 1L));
    }

    @Test
    void deleteInvoice() {
        // given
        Long invoiceId = 1L;

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(invoiceRepository.findById(any())).thenReturn(Optional.of(mockInvoice));
        when(paymentRepository.existsById(invoiceId)).thenReturn(false);

        // when
        invoiceService.deleteInvoice(invoiceId);

        //then
        verify(invoiceRepository, times(1)).delete(mockInvoice);
    }

    @Test
    void getInvoice() {
        // given
        Long invoiceId = 1L;

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));
        when(invoiceRepository.findById(any())).thenReturn(Optional.of(mockInvoice));

        // when
        GetInvoiceDto getInvoiceDto = invoiceService.getInvoice(invoiceId);

        //then
        assertEquals(mockInvoice.getContract().getId(), getInvoiceDto.getContractId());;
    }

    @Test
    void testGetInvoiceList_Success() {
        // given
        Long contractId = 1L;
        Long paymentStatusId = 1L;
        Long paymentTypeId = 1L;
        LocalDate startContractDate = LocalDate.now().minusDays(10);
        LocalDate endContractDate = LocalDate.now();
        LocalDate startDueDate = LocalDate.now().minusDays(10);
        LocalDate endDueDate = LocalDate.now();
        LocalDate startCreatedAt = LocalDate.now().minusDays(10);
        LocalDate endCreatedAt = LocalDate.now();
        Pageable pageable = PageRequest.of(0, 10);

        when(SecurityUtil.getCurrentUser()).thenReturn(Optional.of(mockUser));

        List<Invoice> invoiceList = new ArrayList<>();
        invoiceList.add(mockInvoice);
        Page<Invoice> invoicePage = new PageImpl<>(invoiceList, pageable, invoiceList.size());

        when(invoiceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(invoicePage);

        Page<GetInvoiceListDto> result = invoiceService.getInvoiceList(
                contractId, "", "", paymentStatusId, paymentTypeId, startContractDate, endContractDate, startDueDate,
                endDueDate, startCreatedAt, endCreatedAt, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(mockInvoice.getChargeAmount(), result.getContent().get(0).getChargeAmount());
    }
}
