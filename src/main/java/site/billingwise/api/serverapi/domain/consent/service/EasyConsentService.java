package site.billingwise.api.serverapi.domain.consent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetBasicItemDto;
import site.billingwise.api.serverapi.domain.consent.dto.response.GetContractInfoDto;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.contract.repository.ContractRepository;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.item.repository.ItemRepository;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EasyConsentService {

    private final ItemRepository itemRepository;
    private final ContractRepository contractRepository;

    public List<GetBasicItemDto> getBasicItemList(Long clientId) {
        return itemRepository.findAllByClientIdAndIsBasic(clientId, true)
                .stream().map((item) -> GetBasicItemDto.toDto(item))
                .collect(Collectors.toList());
    }

    public GetContractInfoDto getContractInfo(Long contractId) {
        Contract contract = contractRepository.findWithItemWithMemberById(contractId)
                .orElseThrow(() -> new GlobalException(FailureInfo.NOT_EXIST_CONTRACT));

        if(contract.getPaymentType() != PaymentType.AUTO_TRANSFER) {
            throw new GlobalException(FailureInfo.NOT_CMS);
        }

        if(!contract.getIsEasyConsent()) {
            throw new GlobalException(FailureInfo.NOT_EASY_CONSENT_CONTRACT);
        }

        if(contract.getContractStatus() != ContractStatus.PENDING) {
            throw new GlobalException(FailureInfo.NOT_PENDING_CONTRACT);
        }

        return GetContractInfoDto.toDto(contract);

    }
}
