package site.billingwise.api.serverapi.domain.contract.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.contract.PaymentType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTypeDto {
    private Long id;
    private String name;

    public static PaymentTypeDto fromEnum(PaymentType paymentType) {
        PaymentTypeDto paymentTypeDto = PaymentTypeDto.builder()
                .id(paymentType.getId())
                .name(paymentType.getName())
                .build();

        return paymentTypeDto;
    }

}
