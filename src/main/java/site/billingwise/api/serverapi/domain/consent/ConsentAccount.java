package site.billingwise.api.serverapi.domain.consent;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.member.Member;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE consent_account SET is_deleted = true WHERE member_id = ?")
@Where(clause = "is_deleted = false")
public class ConsentAccount extends BaseEntity {

    @Id
    @Column(name = "member_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 50, nullable = false)
    private String owner;

    @Column(length = 50, nullable = false)
    private String bank;

    @Column(length = 20, nullable = false)
    private String number;

    @Column(length = 512, nullable = false)
    @Setter
    private String signUrl;
}
