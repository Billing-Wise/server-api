package site.billingwise.api.serverapi.domain.contract;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.common.EnumField;
import site.billingwise.api.serverapi.domain.invoice.Invoice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum PaymentType implements EnumField {

    PAYER_PAYMENT(1L, "납부자 결제"),
    AUTO_TRANSFER(2L, "자동 이체");

    private final Long id;
    private final String name;


}
