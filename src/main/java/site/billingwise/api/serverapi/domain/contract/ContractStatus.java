package site.billingwise.api.serverapi.domain.contract;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.common.EnumField;
import site.billingwise.api.serverapi.global.exception.GlobalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ContractStatus implements EnumField {

    PENDING(1L, "대기"),
    PROGRESS(2L, "진행"),
    END(3L, "종료");

    private final Long id;
    private final String name;


}
