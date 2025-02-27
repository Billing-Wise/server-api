package site.billingwise.api.serverapi.domain.invoice;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;

import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.payment.Payment;
import site.billingwise.api.serverapi.global.converter.InvoiceTypeConverter;
import site.billingwise.api.serverapi.global.converter.PaymentStatusConverter;
import site.billingwise.api.serverapi.global.converter.PaymentTypeConverter;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE invoice SET is_deleted = true WHERE invoice_id = ?")
@Where(clause = "is_deleted = false")
public class Invoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Convert(converter = InvoiceTypeConverter.class)
    @Column(name = "invoice_type_id", nullable = false)
    private InvoiceType invoiceType;

    @Setter
    @Convert(converter = PaymentTypeConverter.class)
    @Column(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @Setter
    @Convert(converter = PaymentStatusConverter.class)
    @Column(name = "payment_status_id", nullable = false)
    private PaymentStatus paymentStatus;

    @Setter
    @Column(nullable = false)
    private Long chargeAmount;

    @Setter
    @Column(nullable = false)
    private LocalDateTime contractDate;

    @Setter
    @Column(nullable = false)
    private LocalDateTime dueDate;

    // @OneToOne(mappedBy = "invoice", fetch = FetchType.LAZY)
    // private Payment payment;
}
