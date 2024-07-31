package site.billingwise.api.serverapi.domain.stats;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.global.converter.InvoiceStatsTypeConverter;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name="invoice_statistics")
@SQLDelete(sql = "UPDATE invoice_statistics SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class InvoiceStats extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "total_invoiced", nullable = false)
    private Long totalInvoiced;

    @Column(name = "total_collected", nullable = false)
    private Long totalCollected;

    @Column(name = "outstanding", nullable = false)
    private Long outstanding;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = true)
    private Integer month;

    @Column(nullable = true)
    private Integer week;

    @Convert(converter = InvoiceStatsTypeConverter.class)
    @Column(name = "type_id", nullable = false)
    private InvoiceStatsType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
