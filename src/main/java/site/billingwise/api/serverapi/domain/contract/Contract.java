package site.billingwise.api.serverapi.domain.contract;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import java.util.Set;
import java.util.TreeSet;

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

    @Setter
    @Convert(converter = InvoiceTypeConverter.class)
    @Column(name = "invoice_type_id", nullable = false)
    private InvoiceType invoiceType;

    @Setter
    @Convert(converter = PaymentTypeConverter.class)
    @Column(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @Setter
    @Convert(converter = ContractStatusConverter.class)
    @Column(name = "contract_status_id", nullable = false)
    private ContractStatus contractStatus;

    @Column(nullable = false)
    private Boolean isSubscription;

    @Setter
    @Column(nullable = false)
    private Long itemPrice;

    @Setter
    @Column(nullable = false)
    private Integer itemAmount;

    @Setter
    @Column(nullable = false)
    private Integer contractCycle;

    @Setter
    @Column(nullable = false)
    private Integer paymentDueCycle;

    @Setter
    @Column(nullable = false)
    private Boolean isEasyConsent;

    @OneToMany(mappedBy = "contract", fetch = FetchType.LAZY)
    private Set<Invoice> invoiceList;

}
