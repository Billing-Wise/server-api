package site.billingwise.api.serverapi.domain.invoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTypeDto {
    private Long id;
    private String name;

    public static InvoiceTypeDto fromEnum(InvoiceType invoiceType) {
        InvoiceTypeDto invoiceTypeDto = InvoiceTypeDto.builder()
                .id(invoiceType.getId())
                .name(invoiceType.getName())
                .build();

        return invoiceTypeDto;
    }
}
