package site.billingwise.api.serverapi.domain.invoice.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.contract.dto.response.PaymentTypeDto;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetInvoiceDto {
    private Long contractId;
    private Long invoiceId;
    private PaymentTypeDto paymentType;
    private InvoiceTypeDto invoiceType;
    private PaymentStatusDto paymentStatus;
    private InvoiceItemDto item;
    private InvoiceMemberDto member;
    private Long chargeAmount;
    private Boolean isSubscription;
    private LocalDateTime contractDate;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
