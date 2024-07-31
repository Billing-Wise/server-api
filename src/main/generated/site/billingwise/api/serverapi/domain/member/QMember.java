package site.billingwise.api.serverapi.domain.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1853889275L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    public final site.billingwise.api.serverapi.domain.user.QClient client;

    public final NumberPath<Long> contractCount = createNumber("contractCount", Long.class);

    public final SetPath<site.billingwise.api.serverapi.domain.contract.Contract, site.billingwise.api.serverapi.domain.contract.QContract> contractList = this.<site.billingwise.api.serverapi.domain.contract.Contract, site.billingwise.api.serverapi.domain.contract.QContract>createSet("contractList", site.billingwise.api.serverapi.domain.contract.Contract.class, site.billingwise.api.serverapi.domain.contract.QContract.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.client = inits.isInitialized("client") ? new site.billingwise.api.serverapi.domain.user.QClient(forProperty("client")) : null;
    }

}

