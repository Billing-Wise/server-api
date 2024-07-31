package site.billingwise.api.serverapi.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClient is a Querydsl query type for Client
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClient extends EntityPathBase<Client> {

    private static final long serialVersionUID = -1867573851L;

    public static final QClient client = new QClient("client");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    public final StringPath authCode = createString("authCode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<site.billingwise.api.serverapi.domain.stats.InvoiceStats, site.billingwise.api.serverapi.domain.stats.QInvoiceStats> invoiceStatsList = this.<site.billingwise.api.serverapi.domain.stats.InvoiceStats, site.billingwise.api.serverapi.domain.stats.QInvoiceStats>createSet("invoiceStatsList", site.billingwise.api.serverapi.domain.stats.InvoiceStats.class, site.billingwise.api.serverapi.domain.stats.QInvoiceStats.class, PathInits.DIRECT2);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final SetPath<site.billingwise.api.serverapi.domain.item.Item, site.billingwise.api.serverapi.domain.item.QItem> itemList = this.<site.billingwise.api.serverapi.domain.item.Item, site.billingwise.api.serverapi.domain.item.QItem>createSet("itemList", site.billingwise.api.serverapi.domain.item.Item.class, site.billingwise.api.serverapi.domain.item.QItem.class, PathInits.DIRECT2);

    public final SetPath<site.billingwise.api.serverapi.domain.member.Member, site.billingwise.api.serverapi.domain.member.QMember> memberList = this.<site.billingwise.api.serverapi.domain.member.Member, site.billingwise.api.serverapi.domain.member.QMember>createSet("memberList", site.billingwise.api.serverapi.domain.member.Member.class, site.billingwise.api.serverapi.domain.member.QMember.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final SetPath<User, QUser> userList = this.<User, QUser>createSet("userList", User.class, QUser.class, PathInits.DIRECT2);

    public QClient(String variable) {
        super(Client.class, forVariable(variable));
    }

    public QClient(Path<? extends Client> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClient(PathMetadata metadata) {
        super(Client.class, metadata);
    }

}

