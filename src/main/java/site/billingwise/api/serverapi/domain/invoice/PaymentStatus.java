package site.billingwise.api.serverapi.domain.invoice;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.common.EnumField;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus implements EnumField {

    UNPAID(1L, "미납"),
    PAID(2L, "완납"),
    PENDING(3L, "대기");

    private final Long id;
    private final String name;

}
