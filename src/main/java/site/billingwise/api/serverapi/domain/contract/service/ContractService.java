package site.billingwise.api.serverapi.domain.contract.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.consent.repository.ConsentAccountRepository;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.contract.dto.request.CreateContractDto;
import site.billingwise.api.serverapi.domain.contract.dto.request.EditContractDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.ContractInvoiceTypeDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.ContractItemDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.ContractMemberDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.ContractStatusDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.CreateBulkContractResultDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.GetContractAllDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.PaymentTypeDto;
import site.billingwise.api.serverapi.domain.contract.dto.response.GetContractDto;
import site.billingwise.api.serverapi.domain.contract.repository.ContractRepository;
import site.billingwise.api.serverapi.domain.contract.repository.ContractSpecification;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.service.ItemService;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.service.MemberService;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.mail.EmailService;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.EnumUtil;
import site.billingwise.api.serverapi.global.util.PoiUtil;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ItemService itemService;
    private final MemberService memberService;
    private final EmailService emailService;

    private final ConsentAccountRepository consentAccountRepository;
    private final ContractRepository contractRepository;

    private final Validator validator;

    private ArrayList<Contract> emailArr;

    @Transactional
    public Long createContract(CreateContractDto createContractDto) {
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        emailArr = new ArrayList<>();

        Contract contract = toEntityFromCreateDto(user.getClient(), createContractDto);

        contractRepository.save(contract);

        emailArr.forEach(c -> {
            emailService.createMailConsent(c.getMember().getEmail(), c.getId());
        });

        return contract.getId();
    }

    @Transactional
    public void editContract(Long contractId, EditContractDto editContractDto) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Contract contract = getEntity(user.getClient(), contractId);

        PaymentType paymentType = EnumUtil.toEnum(PaymentType.class, editContractDto.getPaymentTypeId());
        InvoiceType invoiceType = EnumUtil.toEnum(InvoiceType.class, editContractDto.getInvoiceTypeId());

        boolean existConsent = consentAccountRepository.existsById(contract.getMember().getId());

        boolean consentNeeded = !existConsent && editContractDto.getPaymentTypeId() == 2;
        ContractStatus contractStatus = EnumUtil.toEnum(ContractStatus.class, consentNeeded ? 1L : 2L);

        boolean isEasyConsent = paymentType == PaymentType.PAYER_PAYMENT ? false : editContractDto.getIsEasyConsent();

        if (consentNeeded && isEasyConsent) {
            emailService.createMailConsent(contract.getMember().getEmail(), contract.getId());
        }

        contract.setItemPrice(editContractDto.getItemPrice());
        contract.setItemAmount(editContractDto.getItemAmount());
        contract.setPaymentType(paymentType);
        contract.setInvoiceType(invoiceType);
        contract.setIsEasyConsent(isEasyConsent);
        contract.setContractCycle(editContractDto.getContractCycle());
        contract.setPaymentDueCycle(editContractDto.getPaymentDueCycle());
        contract.setContractStatus(contractStatus);
    }

    public void deleteContract(Long contractId) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Contract contract = getEntity(user.getClient(), contractId);

        contractRepository.delete(contract);
    }

    @Transactional(readOnly = true)
    public GetContractDto getContract(Long contractId) {
        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Contract contract = contractRepository.findWithItemWithMemberById(contractId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONTRACT));

        if (contract.getItem().getClient().getId() != user.getClient().getId()) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        return toGetDtoFromEntity(contract);
    }

    @Transactional(readOnly = true)
    public Page<GetContractAllDto> getContractList(
            Long itemId,
            Long memberId,
            String itemName,
            String memberName,
            Boolean isSubscription,
            Long invoiceTypeId,
            Long contractStatusId,
            Long paymentStatusId,
            Pageable pageable) {

        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        Specification<Contract> spec = ContractSpecification.findContract(
                itemId, memberId, itemName, memberName, isSubscription, invoiceTypeId, contractStatusId,
                paymentStatusId,
                user.getClient().getId());

        Page<Contract> contractList = contractRepository.findAll(spec, pageable);

        Page<GetContractAllDto> getContractList = contractList
                .map((contract) -> toGetAllDtoFromEntity(contract));

        return getContractList;
    }

    @Transactional
    public CreateBulkContractResultDto createContractBulk(MultipartFile file) {
        // user
        User user = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        // dto field
        boolean isSuccess = true;
        List<CreateContractDto> createContractDtoList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        emailArr = new ArrayList<>();

        // row validation test
        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                // 제목행 skip
                if (row.getRowNum() == 0) {
                    continue;
                }

                // 빈 행인지 확인
                if (!PoiUtil.getNotBlank(row, 9)) {
                    continue;
                }

                boolean rowValidation = true;
                CreateContractDto createContractDto = null;

                // 셀 서식 검증
                try {
                    createContractDto = CreateContractDto.builder()
                            .memberId(PoiUtil.getCellLongValue(row.getCell(0)))
                            .itemId(PoiUtil.getCellLongValue(row.getCell(1)))
                            .itemPrice(PoiUtil.getCellLongValue(row.getCell(2)))
                            .itemAmount(PoiUtil.getCellIntValue(row.getCell(3)))
                            .isSubscription(PoiUtil.getCellBooleanValue(row.getCell(4)))
                            .invoiceTypeId(PoiUtil.getCellLongValue(row.getCell(5)))
                            .paymentTypeId(PoiUtil.getCellLongValue(row.getCell(6)))
                            .isEasyConsent(true)
                            .contractCycle(PoiUtil.getCellIntValue(row.getCell(7)))
                            .paymentDueCycle(PoiUtil.getCellIntValue(row.getCell(8)))
                            .build();
                } catch (Exception e) {
                    isSuccess = false;
                    rowValidation = false;
                    errorList.add(row.getRowNum() + "행: " + e.getMessage());
                }

                // dto @valid 검증
                if (rowValidation) {
                    BindingResult bindingResult = new BeanPropertyBindingResult(createContractDto,
                            "createContractDto");
                    validator.validate(createContractDto, bindingResult);

                    if (bindingResult.hasErrors()) {
                        isSuccess = false;
                        rowValidation = false;
                        bindingResult.getAllErrors()
                                .forEach(error -> errorList.add(row.getRowNum() + "행 : " + error.getDefaultMessage()));
                    }
                }

                // db 연관성 검증
                if (rowValidation) {
                    try {
                        Contract contract = toEntityFromCreateDto(user.getClient(), createContractDto);
                        contractRepository.save(contract);

                    } catch (Exception e) {
                        isSuccess = false;
                        rowValidation = false;
                        errorList.add(row.getRowNum() + "행 : " + e.getMessage());
                    }
                }

                if (rowValidation) {
                    createContractDtoList.add(createContractDto);
                }
            }
        } catch (Exception e) {
            throw new GlobalException(FailureInfo.INVALID_FILE);
        }

        if (isSuccess) {
            emailArr.forEach(c -> {
                emailService.createMailConsent(c.getMember().getEmail(), c.getId());
            });
        }

        CreateBulkContractResultDto createBulkResultDto = CreateBulkContractResultDto.builder()
                .isSuccess(isSuccess)
                .contractList(createContractDtoList)
                .errorList(errorList)
                .build();

        return createBulkResultDto;
    }

    public Contract getEntity(Client client, Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONTRACT));

        if (contract.getItem().getClient().getId() != client.getId()) {
            throw new GlobalException(FailureInfo.ACCESS_DENIED);
        }

        return contract;
    }

    private Contract toEntityFromCreateDto(Client client, CreateContractDto createContractDto) {
        Item item = itemService.getEntity(client, createContractDto.getItemId());
        Member member = memberService.getEntity(client, createContractDto.getMemberId());

        PaymentType paymentType = EnumUtil.toEnum(PaymentType.class, createContractDto.getPaymentTypeId());
        InvoiceType invoiceType = EnumUtil.toEnum(InvoiceType.class, createContractDto.getInvoiceTypeId());

        boolean existConsent = consentAccountRepository.existsById(member.getId());
        boolean consentNeeded = !existConsent && createContractDto.getPaymentTypeId() == 2;
        ContractStatus contractStatus = EnumUtil.toEnum(ContractStatus.class, consentNeeded ? 1L : 2L);

        boolean isEasyConsent = paymentType == PaymentType.PAYER_PAYMENT ? false : createContractDto.getIsEasyConsent();

        Contract contract = Contract.builder()
                .member(member)
                .item(item)
                .itemPrice(createContractDto.getItemPrice())
                .itemAmount(createContractDto.getItemAmount())
                .isSubscription(createContractDto.getIsSubscription())
                .paymentType(paymentType)
                .invoiceType(invoiceType)
                .isEasyConsent(isEasyConsent)
                .contractCycle(createContractDto.getContractCycle())
                .paymentDueCycle(createContractDto.getPaymentDueCycle())
                .contractStatus(contractStatus)
                .build();

        if (consentNeeded && isEasyConsent) {
            emailArr.add(contract);
        }

        return contract;
    }

    private GetContractDto toGetDtoFromEntity(Contract contract) {
        Long totalChargeAmount = 0L;
        Long totalUnpaidAmount = 0L;

        for (Invoice invoice : contract.getInvoiceList()) {
            totalChargeAmount += invoice.getChargeAmount();
            if (invoice.getPaymentStatus().getId() == 1) {
                totalUnpaidAmount += invoice.getChargeAmount();
            }
        }

        ContractMemberDto member = ContractMemberDto.builder()
                .id(contract.getMember().getId())
                .name(contract.getMember().getName())
                .email(contract.getMember().getEmail())
                .phone(contract.getMember().getPhone())
                .build();

        ContractItemDto item = ContractItemDto.builder()
                .id(contract.getItem().getId())
                .name(contract.getItem().getName())
                .price(contract.getItemPrice())
                .amount(contract.getItemAmount())
                .build();

        ContractInvoiceTypeDto invoiceType = ContractInvoiceTypeDto.builder()
                .id(contract.getInvoiceType().getId())
                .name(contract.getInvoiceType().getName())
                .build();

        PaymentTypeDto paymentType = PaymentTypeDto.builder()
                .id(contract.getPaymentType().getId())
                .name(contract.getPaymentType().getName())
                .build();

        GetContractDto getContractDto = GetContractDto.builder()
                .id(contract.getId())
                .member(member)
                .item(item)
                .chargeAmount(contract.getItemPrice() * contract.getItemAmount())
                .isSubscription(contract.getIsSubscription())
                .isEasyConsent(contract.getIsEasyConsent())
                .totalChargeAmount(totalChargeAmount)
                .totalUnpaidAmount(totalUnpaidAmount)
                .invoiceType(invoiceType)
                .paymentType(paymentType)
                .contractCycle(contract.getContractCycle())
                .paymentDueCycle(contract.getPaymentDueCycle())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();

        return getContractDto;
    }

    private GetContractAllDto toGetAllDtoFromEntity(Contract contract) {
        ContractInvoiceTypeDto invoiceType = ContractInvoiceTypeDto.builder()
                .id(contract.getInvoiceType().getId())
                .name(contract.getInvoiceType().getName())
                .build();

        PaymentTypeDto paymentType = PaymentTypeDto.builder()
                .id(contract.getPaymentType().getId())
                .name(contract.getPaymentType().getName())
                .build();

        ContractStatusDto contractStatus = ContractStatusDto.builder()
                .id(contract.getContractStatus().getId())
                .name(contract.getContractStatus().getName())
                .build();

        GetContractAllDto getContractAllDto = GetContractAllDto.builder()
                .id(contract.getId())
                .memberName(contract.getMember().getName())
                .itemName(contract.getItem().getName())
                .chargeAmount(contract.getChargeAmount())
                .contractCycle(contract.getContractCycle())
                .paymentDueCycle(contract.getPaymentDueCycle())
                .isSubscription(contract.getIsSubscription())
                .totalUnpaidCount(contract.getTotalUnpaidCount())
                .invoiceType(invoiceType)
                .paymentType(paymentType)
                .contractStatus(contractStatus)
                .build();

        return getContractAllDto;
    }

}
