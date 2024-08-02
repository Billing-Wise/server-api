package site.billingwise.api.serverapi.domain.contract;

import lombok.*;
import site.billingwise.api.serverapi.domain.common.EnumField;

@Getter
@RequiredArgsConstructor
public enum PaymentType implements EnumField {

    PAYER_PAYMENT(1L, "납부자 결제"),
    REALTIME_CMS(2L, "실시간 CMS");

    private final Long id;
    private final String name;


}
