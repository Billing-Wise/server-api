package site.billingwise.api.serverapi.domain.contract;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.member.Member;
import site.billingwise.api.serverapi.global.converter.ContractStatusConverter;
import site.billingwise.api.serverapi.global.converter.InvoiceTypeConverter;
import site.billingwise.api.serverapi.global.converter.PaymentTypeConverter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE contract SET is_deleted = true WHERE contract_id = ?")
@Where(clause = "is_deleted = false")
public class Contract extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Convert(converter = InvoiceTypeConverter.class)
    @Column(name = "invoice_type_id", nullable = false)
    private InvoiceType invoiceType;

    @Convert(converter = PaymentTypeConverter.class)
    @Column(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @Convert(converter = ContractStatusConverter.class)
    @Column(name = "contract_status_id", nullable = false)
    private ContractStatus contractStatus;

    @Column(nullable = false)
    private Boolean isSubscription;

    @Column(nullable = false)
    private Long itemPrice;

    @Column(nullable = false)
    private Integer itemAmount;

    @Column(nullable = false)
    private Integer contractCycle;

    @Column(nullable = false)
    private Integer paymentDueCycle;

    @Column(nullable = false)
    private Boolean isEasyConsent;

    @OneToMany(mappedBy = "contract")
    private List<Invoice> invoiceList = new ArrayList<>();

}
