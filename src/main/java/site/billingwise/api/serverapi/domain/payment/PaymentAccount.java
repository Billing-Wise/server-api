package site.billingwise.api.serverapi.domain.payment;

import jakarta.persistence.*;
import lombok.*;
import site.billingwise.api.serverapi.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentAccount extends BaseEntity {

    @Id
    @Column(name = "invoice_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "invoice_id", nullable = false)
    private Payment payment;

    @Column(length = 50, nullable = false)
    private String owner;

    @Column(length = 50, nullable = false)
    private String bank;

    @Column(length = 20, nullable = false)
    private String number;
}
