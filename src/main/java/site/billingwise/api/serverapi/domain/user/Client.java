package site.billingwise.api.serverapi.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import site.billingwise.api.serverapi.domain.common.BaseEntity;
import site.billingwise.api.serverapi.domain.item.Item;
import site.billingwise.api.serverapi.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE client SET is_deleted = true WHERE client_id = ?")
@Where(clause = "is_deleted = false")
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 60, unique = true, nullable = false)
    private String authCode;

    @Column(length = 20, nullable = false)
    private String phone;

    @OneToMany(mappedBy = "client")
    private List<User> userList = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    private List<Member> memberList = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    private List<Item> itemList = new ArrayList<>();

}
