package site.billingwise.api.serverapi.domain.invoice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.contract.dto.response.PaymentTypeDto;
import site.billingwise.api.serverapi.domain.contract.service.ContractService;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;
import site.billingwise.api.serverapi.domain.invoice.dto.request.CreateInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.request.EditInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.GetInvoiceDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.GetInvoiceListDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.InvoiceItemDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.InvoiceMemberDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.InvoiceTypeDto;
import site.billingwise.api.serverapi.domain.invoice.dto.response.PaymentStatusDto;
import site.billingwise.api.serverapi.domain.invoice.repository.InvoiceRepository;
import site.billingwise.api.serverapi.domain.invoice.repository.InvoiceSpecification;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.payment.repository.PaymentRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.mail.EmailService;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.EnumUtil;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final ContractService contractService;
    private final EmailService emailService;

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    // 청구서 전송
    public void sendInvoice(Long invoiceId) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Invoice invoice = getEntity(user.getClient(), invoiceId);

        if (invoice.getPaymentType() == PaymentType.AUTO_TRANSFER) {
            throw new GlobalException(FailureInfo.INVALID_PAYMENTTYPE);
        }

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new GlobalException(FailureInfo.PAID_INVOICE);
        }

        Member member = invoice.getContract().getMember();

        emailService.createMailInvoice(member.getEmail(), invoiceId);
    }

    // 청구 생성
    @Transactional
    public void createInvoice(CreateInvoiceDto createInvoiceDto) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Contract contract = contractService.getEntity(user.getClient(), createInvoiceDto.getContractId());
        PaymentType paymentType = EnumUtil.toEnum(PaymentType.class, createInvoiceDto.getPaymentTypeId());

        if (contract.getContractStatus() != ContractStatus.PROGRESS) {
            throw new GlobalException(FailureInfo.NOT_PROGRESS_CONTRACT);
        }

        // 약정일이 다음 날 이후인지 확인
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        if (createInvoiceDto.getContractDate().isBefore(tomorrow)) {
            throw new GlobalException(FailureInfo.INVALID_CONTRACT_DATE);
        }

        // 납부기한이 약정일 이후인지 확인
        if (createInvoiceDto.getDueDate().isBefore(createInvoiceDto.getContractDate())) {
            throw new GlobalException(FailureInfo.INVALID_DUE_DATE);
        }

        // 납부자 결제인 계약에서 실시간 CMS 청구 등록 불가능
        if (contract.getPaymentType() == PaymentType.PAYER_PAYMENT && paymentType == PaymentType.AUTO_TRANSFER) {
            throw new GlobalException(FailureInfo.INVALID_INVOICE_PAYMENTTYPE);
        }

        // 약정일에 해당하는 월에 이미 청구가 존재하는지 확인
        LocalDateTime startDate = LocalDateTime.of(createInvoiceDto.getContractDate().getYear(),
                createInvoiceDto.getContractDate().getMonthValue(), 1, 0, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

        if (invoiceRepository.existByMonthlyInvoice(contract.getId(), startDate, endDate)) {
            throw new GlobalException(FailureInfo.DUPLICATE_INVOICE);
        }

        // 청구 데이터 생성
        Invoice invoice = Invoice.builder()
                .contract(contract)
                .invoiceType(InvoiceType.MANUAL)
                .paymentType(paymentType)
                .paymentStatus(PaymentStatus.PENDING)
                .chargeAmount(createInvoiceDto.getChargeAmount())
                .contractDate(createInvoiceDto.getContractDate().atStartOfDay())
                .dueDate(createInvoiceDto.getDueDate().atStartOfDay())
                .build();

        invoiceRepository.save(invoice);
    }

    // 청구 수정
    @Transactional
    public GetInvoiceDto editInvoice(Long invoiceId, EditInvoiceDto editInvoiceDto) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Invoice invoice = getEntity(user.getClient(), invoiceId);

        PaymentType paymentType = EnumUtil.toEnum(PaymentType.class, editInvoiceDto.getPaymentTypeId());

        // 이미 결제된 청구서인지 확인
        if (paymentRepository.existsById(invoiceId)) {
            throw new GlobalException(FailureInfo.PAID_INVOICE);
        }

        // 수정 약정일이 다음 날 이후인지 확인
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        if (editInvoiceDto.getContractDate().isBefore(tomorrow)) {
            throw new GlobalException(FailureInfo.INVALID_CONTRACT_DATE);
        }

        // 수정 약정일과 현재 약정일과 연월이 같은지 확인
        if (editInvoiceDto.getContractDate().getYear() != invoice.getContractDate().getYear()
                || editInvoiceDto.getContractDate().getMonth() != invoice.getContractDate().getMonth()) {
            throw new GlobalException(FailureInfo.DIFFERENT_MONTH);
        }

        // 납부기한이 약정일 이후인지 확인
        if (editInvoiceDto.getDueDate().isBefore(editInvoiceDto.getContractDate())) {
            throw new GlobalException(FailureInfo.INVALID_DUE_DATE);
        }

        // 납부자 결제인 계약에서 실시간 CMS 청구 등록 불가능
        if (invoice.getContract().getPaymentType() == PaymentType.PAYER_PAYMENT
                && paymentType == PaymentType.AUTO_TRANSFER) {
            throw new GlobalException(FailureInfo.INVALID_INVOICE_PAYMENTTYPE);
        }

        // 청구 데이터 생성
        invoice.setPaymentType(paymentType);
        invoice.setChargeAmount(editInvoiceDto.getChargeAmount());
        invoice.setContractDate(editInvoiceDto.getContractDate().atStartOfDay());
        invoice.setDueDate(editInvoiceDto.getDueDate().atStartOfDay());

        return toGetDtoFromEntity(invoice);
    }

    // 청구 삭제
    @Transactional
    public void deleteInvoice(Long invoiceId) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Invoice invoice = getEntity(user.getClient(), invoiceId);

        // 이미 결제된 청구서인지 확인
        if (paymentRepository.existsById(invoiceId)) {
            throw new GlobalException(FailureInfo.PAID_INVOICE);
        }

        invoiceRepository.delete(invoice);
    }

    // 청구 상세 조회
    @Transactional(readOnly = true)
    public GetInvoiceDto getInvoice(Long invoiceId) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Invoice invoice = getEntity(user.getClient(), invoiceId);

        return toGetDtoFromEntity(invoice);
    }

    // 청구 목록 조회
    @Transactional(readOnly = true)
    public Page<GetInvoiceListDto> getInvoiceList(
            Long contractId,
            String itemName,
            String memberName,
            Long paymentStatusId,
            Long paymentTypeId,
            LocalDate startContractDate,
            LocalDate endContractDate,
            LocalDate startDueDate,
            LocalDate endDueDate,
            LocalDate startCreatedAt,
            LocalDate endCreatedAt,
            Pageable pageable) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Specification<Invoice> spec = InvoiceSpecification.findContract(
                contractId, itemName, memberName, paymentStatusId, paymentTypeId, startContractDate, endContractDate,
                startDueDate,
                endDueDate, startCreatedAt, endCreatedAt,
                user.getClient().getId());

        Page<Invoice> invoiceList = invoiceRepository.findAll(spec, pageable);

        Page<GetInvoiceListDto> getInvoiceList = invoiceList.map((invoice) -> toGetListDtoFromEntity(invoice));

        return getInvoiceList;
    }

    // 유효성 검증 후 엔티티 반환
    public Invoice getEntity(Client client, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_INVOICE));

        if (invoice.getContract().getItem().getClient().getId() != client.getId()) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        return invoice;
    }

    // 상세 DTO로 변환
    private GetInvoiceDto toGetDtoFromEntity(Invoice invoice) {
        Contract contract = invoice.getContract();

        InvoiceItemDto invoiceItemDto = InvoiceItemDto.builder()
                .itemId(contract.getItem().getId())
                .name(contract.getItem().getName())
                .price(invoice.getContract().getItemPrice())
                .amount(invoice.getContract().getItemAmount())
                .build();

        InvoiceMemberDto invoiceMemberDto = InvoiceMemberDto.builder()
                .memberId(contract.getMember().getId())
                .name(contract.getMember().getName())
                .email(contract.getMember().getEmail())
                .phone(contract.getMember().getPhone())
                .build();

        GetInvoiceDto getInvoiceDto = GetInvoiceDto.builder()
                .contractId(invoice.getContract().getId())
                .invoiceId(invoice.getId())
                .paymentType(PaymentTypeDto.fromEnum(invoice.getPaymentType()))
                .invoiceType(InvoiceTypeDto.fromEnum(invoice.getInvoiceType()))
                .paymentStatus(PaymentStatusDto.fromEnum(invoice.getPaymentStatus()))
                .item(invoiceItemDto)
                .member(invoiceMemberDto)
                .chargeAmount(invoice.getChargeAmount())
                .isSubscription(contract.getIsSubscription())
                .contractDate(invoice.getContractDate())
                .dueDate(invoice.getDueDate())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();

        return getInvoiceDto;
    }

    // 목록 DTO로 변환
    private GetInvoiceListDto toGetListDtoFromEntity(Invoice invoice) {
        Contract contract = invoice.getContract();

        GetInvoiceListDto getInvoiceListDto = GetInvoiceListDto.builder()
                .invoiceId(invoice.getId())
                .contractId(contract.getId())
                .memberName(contract.getMember().getName())
                .itemName(contract.getItem().getName())
                .chargeAmount(invoice.getChargeAmount())
                .paymentType(PaymentTypeDto.fromEnum(invoice.getPaymentType()))
                .paymentStatus(PaymentStatusDto.fromEnum(invoice.getPaymentStatus()))
                .contractDate(invoice.getContractDate())
                .dueDate(invoice.getDueDate())
                .createdAt(invoice.getCreatedAt())
                .build();

        return getInvoiceListDto;
    }

}
