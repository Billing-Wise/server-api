package site.billingwise.api.serverapi.domain.invoice.dto.response;

import java.time.LocalDateTime;

import site.billingwise.api.serverapi.domain.contract.dto.response.PaymentTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetInvoiceListDto {
    private Long invoiceId;
    private Long contractId;
    private String memberName;
    private String itemName;
    private Long chargeAmount;
    private PaymentTypeDto paymentType;
    private PaymentStatusDto paymentStatus;
    private LocalDateTime contractDate;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
}
