package site.billingwise.api.serverapi.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.invoice.Invoice;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPayerPayInvoiceDto {

    private String memberName;
    private String memberEmail;
    private String memberPhone;
    private String itemName;
    private Integer itemAmount;
    private Long totalPrice;
    private LocalDateTime contractDate;
    private LocalDateTime dueDate;

    public static GetPayerPayInvoiceDto toDto(Invoice invoice) {
        return GetPayerPayInvoiceDto.builder()
                .memberName(invoice.getContract().getMember().getName())
                .memberEmail(invoice.getContract().getMember().getEmail())
                .memberPhone(invoice.getContract().getMember().getPhone())
                .itemName(invoice.getContract().getItem().getName())
                .itemAmount(invoice.getContract().getItemAmount())
                .totalPrice(invoice.getChargeAmount())
                .contractDate(invoice.getContractDate())
                .dueDate(invoice.getDueDate())
                .build();
    }
}
