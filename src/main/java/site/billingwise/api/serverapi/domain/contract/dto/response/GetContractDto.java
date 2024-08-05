package site.billingwise.api.serverapi.domain.contract.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetContractDto {

    private Long id;
    private ContractMemberDto member;
    private ContractItemDto item;
    private Long chargeAmount;
    private boolean isSubscription;
    private boolean isEasyConsent;
    private Long totalChargeAmount;
    private Long totalUnpaidAmount;
    private ContractInvoiceTypeDto invoiceType;
    private PaymentTypeDto paymentType;
    private Integer contractCycle;
    private Integer paymentDueCycle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ContractStatusDto contractStatus;
    
}
