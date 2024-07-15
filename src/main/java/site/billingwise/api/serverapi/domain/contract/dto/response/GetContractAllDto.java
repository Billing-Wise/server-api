package site.billingwise.api.serverapi.domain.contract.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetContractAllDto {
    private Long id;
    private String memberName;
    private String itemName;
    private Long chargeAmount;
    private Integer contractCycle;
    private Integer paymentDueCycle;
    private boolean isSubscription;
    private ContractStatusDto contractStatus;
    private ContractInvoiceTypeDto invoiceType;
    private PaymentTypeDto paymentType;
    private Long totalUnpaidCount;
}
