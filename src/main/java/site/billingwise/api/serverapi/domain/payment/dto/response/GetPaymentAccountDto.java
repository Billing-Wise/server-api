package site.billingwise.api.serverapi.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import site.billingwise.api.serverapi.domain.payment.dto.response.GetPaymentDto;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetPaymentAccountDto extends GetPaymentDto {
    private String number;
    private String bank;
    private String owner;
}
