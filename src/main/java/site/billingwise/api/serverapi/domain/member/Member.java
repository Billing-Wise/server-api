package site.billingwise.api.serverapi.domain.member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.invoice.Invoice;
import site.billingwise.api.serverapi.domain.member.dto.response.GetMemberDto;
import site.billingwise.api.serverapi.domain.user.Client;

import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "client_id", "email" })
})
@SQLDelete(sql = "UPDATE member SET is_deleted = true WHERE member_id = ?")
@Where(clause = "is_deleted = false")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Setter
    @Column(length = 50, nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String description;

    @Setter
    @Column(nullable = false)
    private String email;

    @Setter
    @Column(length = 20, nullable = false)
    private String phone;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private ConsentAccount consentAccount;

    @OneToMany(mappedBy = "member")
    private Set<Contract> contractList;

    public GetMemberDto toDto() {
        long contractCount = 0L;
        long unPaidCount = 0L;
        long totalInvoiceAmount = 0L;
        long totalUnpaidAmount = 0L;

        for (Contract contract : this.getContractList()) {
            boolean isUnpaid = false;

            for (Invoice invoice : contract.getInvoiceList()) {
                totalInvoiceAmount += invoice.getChargeAmount();
                // 이 조건문의 조건 수정해야합니다.
                if (invoice.getPaymentStatus().getId() == 1) {
                    totalUnpaidAmount += invoice.getChargeAmount();
                    isUnpaid = true;
                }
            }

            contractCount++;
            if (isUnpaid) {
                unPaidCount++;
            }
        }

        GetMemberDto getMemberDetailDto = GetMemberDto.builder()
                .id(this.getId())
                .name(this.getName())
                .email(this.getEmail())
                .phone(this.getPhone())
                .description(this.getDescription())
                .contractCount(contractCount)
                .unPaidCount(unPaidCount)
                .totalInvoiceAmount(totalInvoiceAmount)
                .totalUnpaidAmount(totalUnpaidAmount)
                .contractCount(contractCount)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();

        return getMemberDetailDto;
    }
}
