package site.billingwise.api.serverapi.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.payment.Payment;
import site.billingwise.api.serverapi.domain.payment.PaymentAccount;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayerPayAccountDto {
    @NotBlank(message = "계좌 소유주를 입력하세요.")
    private String owner;

    @NotBlank(message = "은행을 입력하세요.")
    private String bank;

    @NotBlank(message = "계좌번호를 입력하세요.")
    @Pattern(regexp = "^\\d{10,20}$", message = "유효한 계좌번호 형식이 아닙니다.")
    private String number;

    public PaymentAccount toEntity(Payment payment) {
        return PaymentAccount.builder()
                .id(payment.getId())
                .payment(payment)
                .number(this.number)
                .bank(this.bank)
                .owner(this.owner)
                .build();
    }
}
