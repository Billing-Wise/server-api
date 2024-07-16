package site.billingwise.api.serverapi.domain.invoice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusDto {
    private Long id;
    private String name;

    public static PaymentStatusDto fromEnum(PaymentStatus paymentStatus) {
        PaymentStatusDto paymentStatusDto = PaymentStatusDto.builder()
                .id(paymentStatus.getId())
                .name(paymentStatus.getName())
                .build();

        return paymentStatusDto;
    }
}
