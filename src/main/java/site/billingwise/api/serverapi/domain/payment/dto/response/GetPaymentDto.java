package site.billingwise.api.serverapi.domain.payment.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetPaymentDto {
    private Long invoiceId;
    private Long payAmoount;
    private LocalDateTime createAt;
}
