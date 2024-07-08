package site.billingwise.api.serverapi.domain.item;

import jakarta.persistence.*;
import lombok.*;
import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.user.Client;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(length = 512, nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean isBasic;

    @OneToMany(mappedBy = "item")
    private List<Contract> contractList = new ArrayList<>();

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
