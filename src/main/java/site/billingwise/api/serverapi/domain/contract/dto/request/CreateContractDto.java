package site.billingwise.api.serverapi.domain.contract.dto.request;

import jakarta.validation.constraints.Max;
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
public class CreateContractDto {

    @NotNull(message = "회원 정보는 필수 입력값입니다.")
    private Long memberId;

    @NotNull(message = "상품 정보는 필수 입력값입니다.")
    private Long itemId;

    @NotNull(message = "금액 정보는 필수 입력값입니다.")
    @Min(value = 1, message = "금액은 1원 이상이어야 합니다.")
    private Long itemPrice;

    @NotNull(message = "금액 정보는 필수 입력값입니다.")
    @Min(value = 1, message = "상품 수량은 1 이상이어야 합니다.")
    private Integer itemAmount;

    @NotNull(message = "구독 여부 필수 입력값입니다.")
    private Boolean isSubscription;

    @NotNull(message = "청구 타입은 필수 입력값입니다.")
    private Long invoiceTypeId;

    @NotNull(message = "결제 수단은 필수 입력값입니다.")
    private Long paymentTypeId;

    @NotNull(message = "간편 동의 여부는 필수 입력값입니다.")
    private Boolean isEasyConsent;

    @NotNull(message = "약정일은 필수 입력값입니다.")
    @Min(value = 1, message = "약정일은 1일~30일 사이로 지정하여야합니다.")
    @Max(value = 30, message = "약정일은 1일~30일 사이로 지정하여야합니다.")
    private Integer contractCycle;

    @NotNull(message = "납부 기한은 필수 입력값입니다.")
    @Min(value = 0, message = "납부 기한은 0일 이상이어야합니다.")
    private Integer paymentDueCycle;
    
}
