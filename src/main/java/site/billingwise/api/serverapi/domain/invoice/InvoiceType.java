package site.billingwise.api.serverapi.domain.invoice;

import jakarta.persistence.*;
import lombok.*;
import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.contract.Contract;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InvoiceType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_type_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @OneToMany(mappedBy = "invoiceType")
    private List<Invoice> invoiceList = new ArrayList<>();

    @OneToMany(mappedBy = "invoiceType")
    private List<Contract> contractList = new ArrayList<>();

}
