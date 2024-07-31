package site.billingwise.api.serverapi.domain.consent;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConsentAccount is a Querydsl query type for ConsentAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConsentAccount extends EntityPathBase<ConsentAccount> {

    private static final long serialVersionUID = 1451564688L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConsentAccount consentAccount = new QConsentAccount("consentAccount");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    public final StringPath bank = createString("bank");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final site.billingwise.api.serverapi.domain.member.QMember member;

    public final StringPath number = createString("number");

    public final StringPath owner = createString("owner");

    public final StringPath signUrl = createString("signUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QConsentAccount(String variable) {
        this(ConsentAccount.class, forVariable(variable), INITS);
    }

    public QConsentAccount(Path<? extends ConsentAccount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConsentAccount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConsentAccount(PathMetadata metadata, PathInits inits) {
        this(ConsentAccount.class, metadata, inits);
    }

    public QConsentAccount(Class<? extends ConsentAccount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new site.billingwise.api.serverapi.domain.member.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

