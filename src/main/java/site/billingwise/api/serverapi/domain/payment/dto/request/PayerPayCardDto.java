package site.billingwise.api.serverapi.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.payment.Payment;
import site.billingwise.api.serverapi.domain.payment.PaymentCard;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayerPayCardDto {

    @NotBlank(message = "카드 소유주를 입력하세요.")
    private String owner;

    @NotBlank(message = "카드사를 입력하세요.")
    private String company;

    @NotBlank(message = "카드 번호를 입력하세요.")
    @Pattern(regexp = "^\\d{16}$", message = "유효한 카드번호 형식이 아닙니다.")
    private String number;

    public PaymentCard toEntity(Payment payment) {
        return PaymentCard.builder()
                .id(payment.getId())
                .payment(payment)
                .number(this.number)
                .company(this.company)
                .owner(this.owner)
                .build();
    }

}
