package site.billingwise.api.serverapi.domain.payment.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetPaymentAccountDto extends GetPaymentDto {
    private String number;
    private String bank;
    private String owner;
}
