package site.billingwise.api.serverapi.domain.consent.dto.response;

import lombok.Builder;
import lombok.Getter;
import site.billingwise.api.serverapi.domain.contract.Contract;

@Builder
@Getter
public class GetContractInfoDto {
    private Long contractId;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String memberPhone;
    private Long itemId;
    private String itemName;
    private Integer itemAmount;
    private Long totalPrice;
    private Boolean isSubscription;
    private Integer contractCycle;

    public static GetContractInfoDto toDto(Contract contract) {
        return GetContractInfoDto.builder()
                .contractId(contract.getId())
                .memberId(contract.getMember().getId())
                .memberName(contract.getMember().getName())
                .memberEmail(contract.getMember().getEmail())
                .memberPhone(contract.getMember().getPhone())
                .itemId(contract.getItem().getId())
                .itemName(contract.getItem().getName())
                .itemAmount(contract.getItemAmount())
                .totalPrice(contract.getItemPrice() * contract.getItemAmount())
                .isSubscription(contract.getIsSubscription())
                .contractCycle(contract.getContractCycle())
                .build();
    }
}
