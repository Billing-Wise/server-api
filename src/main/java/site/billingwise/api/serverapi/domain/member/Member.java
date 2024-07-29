package site.billingwise.api.serverapi.domain.member;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.user.Client;

import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;

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
    @Column(nullable = true)
    private String description;

    @Setter
    @Column(nullable = false)
    private String email;

    @Setter
    @Column(length = 20, nullable = false)
    private String phone;

    @Formula("(SELECT COUNT(*) FROM contract ct WHERE ct.member_id = member_id and ct.is_deleted = false)")
    private Long contractCount;

    @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
    private Set<Contract> contractList;
}
