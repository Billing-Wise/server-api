package site.billingwise.api.serverapi.domain.item;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = -1771828955L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItem item = new QItem("item");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    public final site.billingwise.api.serverapi.domain.user.QClient client;

    public final NumberPath<Long> contractCount = createNumber("contractCount", Long.class);

    public final ListPath<site.billingwise.api.serverapi.domain.contract.Contract, site.billingwise.api.serverapi.domain.contract.QContract> contractList = this.<site.billingwise.api.serverapi.domain.contract.Contract, site.billingwise.api.serverapi.domain.contract.QContract>createList("contractList", site.billingwise.api.serverapi.domain.contract.Contract.class, site.billingwise.api.serverapi.domain.contract.QContract.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final BooleanPath isBasic = createBoolean("isBasic");

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final StringPath name = createString("name");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QItem(String variable) {
        this(Item.class, forVariable(variable), INITS);
    }

    public QItem(Path<? extends Item> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QItem(PathMetadata metadata, PathInits inits) {
        this(Item.class, metadata, inits);
    }

    public QItem(Class<? extends Item> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.client = inits.isInitialized("client") ? new site.billingwise.api.serverapi.domain.user.QClient(forProperty("client")) : null;
    }

}

