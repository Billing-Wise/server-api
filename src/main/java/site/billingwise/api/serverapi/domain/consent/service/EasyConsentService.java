package site.billingwise.api.serverapi.domain.consent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.domain.member.repository.MemberRepository;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.user.repository.ClientRepository;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EasyConsentService {

    private final ConsentService consentService;
    private final ItemRepository itemRepository;
    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;
    private final MemberRepository memberRepository;
    private final ConsentAccountRepository consentAccountRepository;

    @Transactional(readOnly = true)
    public List<GetBasicItemDto> getBasicItemList(Long clientId) {
        return itemRepository.findAllByClientIdAndIsBasic(clientId, true)
                .stream().map((item) -> GetBasicItemDto.toDto(item))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GetContractInfoDto getContractInfo(Long contractId) {
        Contract contract = contractRepository.findWithItemWithMemberById(contractId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONTRACT));

        validateEasyConsentContract(contract);

        return GetContractInfoDto.toDto(contract);

    }

    @Transactional
    public void consentForNonMember(
            Long clientId,
            ConsentWithNonMemberDto dto,
            MultipartFile multipartFile) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CLIENT));

        Item item = itemRepository.findByIdAndClientIdAndIsBasic(dto.getItemId(), clientId, true)
                .orElseThrow(() -> new GlobalException(FailureInfo.ITEM_NOT_FOUND));

        Member member = Member.builder()
                .client(client)
                .email(dto.getMemberEmail())
                .phone(dto.getMemberPhone())
                .name(dto.getMemberName())
                .description(" ")
                .build();

        memberRepository.save(member);

        ConsentAccount consentAccount = ConsentAccount.builder()
                .id(member.getId())
                .member(member)
                .owner(dto.getAccountOwner())
                .bank(dto.getAccountBank())
                .number(dto.getAccountNumber())
                .signUrl(consentService.uploadImage(multipartFile))
                .build();

        consentAccountRepository.save(consentAccount);

        Contract contract = Contract.builder()
                .member(member)
                .item(item)
                .invoiceType(InvoiceType.AUTO)
                .paymentType(PaymentType.REALTIME_CMS)
                .contractStatus(ContractStatus.PROGRESS)
                .isSubscription(dto.getIsSubscription())
                .itemPrice(item.getPrice())
                .itemAmount(dto.getItemAmount())
                .contractCycle(dto.getContractCycle())
                .paymentDueCycle(dto.getContractCycle())
                .isEasyConsent(true)
                .build();

        contractRepository.save(contract);


    }

    @Transactional
    public void consentForMember(Long contractId, RegisterConsentDto registerConsentDto, MultipartFile multipartFile) {

        Contract contract = contractRepository.findWithMemberById(contractId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONTRACT));

        validateEasyConsentContract(contract);

        contract.setContractStatus(ContractStatus.PROGRESS);

        if (consentAccountRepository.existsById(contract.getMember().getId())) {
            throw new GlobalException(FailureInfo.ALREADY_EXIST_CONSENT);
        }

        List<Contract> contractList = contractRepository
                .findAllByMemberAndPaymentTypeAndContractStatus(
                        contract.getMember(),
                        PaymentType.REALTIME_CMS,
                        ContractStatus.PENDING
                );

        for (Contract c : contractList) {
            c.setContractStatus(ContractStatus.PROGRESS);
        }

        consentAccountRepository.save(registerConsentDto.toEntity(
                contract.getMember(),
                consentService.uploadImage(multipartFile))
        );

    }

    private void validateEasyConsentContract(Contract contract) {
        if (contract.getPaymentType() != PaymentType.REALTIME_CMS) {
            throw new GlobalException(FailureInfo.NOT_CMS);
        }

        if (!contract.getIsEasyConsent()) {
            throw new GlobalException(FailureInfo.NOT_EASY_CONSENT_CONTRACT);
        }

        if (contract.getContractStatus() != ContractStatus.PENDING) {
            throw new GlobalException(FailureInfo.NOT_PENDING_CONTRACT);
        }
    }
}
