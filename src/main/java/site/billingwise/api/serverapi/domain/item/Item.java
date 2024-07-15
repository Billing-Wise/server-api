package site.billingwise.api.serverapi.domain.item;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;

import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.contract.Contract;
import site.billingwise.api.serverapi.domain.item.dto.response.GetItemDto;
import site.billingwise.api.serverapi.domain.user.Client;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Formula;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE item SET is_deleted = true WHERE item_id = ?")
@Where(clause = "is_deleted = false")
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference
    private Client client;  

    @Setter
    @Column(length = 50, nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String description;

    @Setter
    @Column(nullable = false)
    private Long price;

    @Setter
    @Column(length = 512, nullable = false)
    private String imageUrl;

    @Setter
    @Column(nullable = false)
    private Boolean isBasic;

    @OneToMany(mappedBy = "item", cascade = ALL, orphanRemoval = true)
    private List<Contract> contractList = new ArrayList<>();

    @Formula("(SELECT COUNT(*) FROM contract ct WHERE ct.item_id = item_id and ct.is_deleted = false)")
    private Long contractCount;

    public GetItemDto toDto() {
        GetItemDto getItemDto = GetItemDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .description(description)
                .imageUrl(imageUrl)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .contractCount(contractCount)
                .isBasic(isBasic)
                .build();

        return getItemDto;
    }
}

