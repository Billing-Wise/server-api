package site.billingwise.api.serverapi.domain.invoice.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditInvoiceDto {
    @NotNull(message = "결제 수단은 필수 입력값입니다.")
    private Long paymentTypeId;

    @NotNull(message = "금액 정보는 필수 입력값입니다.")
    @Min(value = 1, message="금액은 1원 이상이어야합니다.")
    private Long chargeAmount;

    @NotNull(message = "약정일은 필수 입력값입니다.")
    private LocalDate contractDate;

    @NotNull(message = "결제 기한은 필수 입력값입니다.")
    private LocalDate dueDate;
}
