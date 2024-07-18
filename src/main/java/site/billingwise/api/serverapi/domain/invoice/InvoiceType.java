package site.billingwise.api.serverapi.domain.invoice;

import lombok.*;
import site.billingwise.api.serverapi.domain.common.EnumField;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum InvoiceType implements EnumField {

    AUTO(1L, "자동 청구"),
    MANUAL(2L, "수동 청구");

    private final Long id;
    private final String name;

}
