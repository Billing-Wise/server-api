package site.billingwise.api.serverapi.domain.stats;

import lombok.*;
import site.billingwise.api.serverapi.domain.common.EnumField;

@Getter
@RequiredArgsConstructor
public enum InvoiceStatsType implements EnumField {

    WEEKLY(1L, "주간"),
    MONTHLY(2L, "월간");

    private final Long id;
    private final String name;

}