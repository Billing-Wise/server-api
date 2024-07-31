package site.billingwise.api.serverapi.domain.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPaymentAccount is a Querydsl query type for PaymentAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentAccount extends EntityPathBase<PaymentAccount> {

    private static final long serialVersionUID = -515447880L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPaymentAccount paymentAccount = new QPaymentAccount("paymentAccount");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    public final StringPath bank = createString("bank");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final StringPath number = createString("number");

    public final StringPath owner = createString("owner");

    public final QPayment payment;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPaymentAccount(String variable) {
        this(PaymentAccount.class, forVariable(variable), INITS);
    }

    public QPaymentAccount(Path<? extends PaymentAccount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPaymentAccount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPaymentAccount(PathMetadata metadata, PathInits inits) {
        this(PaymentAccount.class, metadata, inits);
    }

    public QPaymentAccount(Class<? extends PaymentAccount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.payment = inits.isInitialized("payment") ? new QPayment(forProperty("payment"), inits.get("payment")) : null;
    }

}

