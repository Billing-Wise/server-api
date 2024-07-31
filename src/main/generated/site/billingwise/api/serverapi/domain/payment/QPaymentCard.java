package site.billingwise.api.serverapi.domain.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPaymentCard is a Querydsl query type for PaymentCard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentCard extends EntityPathBase<PaymentCard> {

    private static final long serialVersionUID = -1123187355L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPaymentCard paymentCard = new QPaymentCard("paymentCard");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    public final StringPath company = createString("company");

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

    public QPaymentCard(String variable) {
        this(PaymentCard.class, forVariable(variable), INITS);
    }

    public QPaymentCard(Path<? extends PaymentCard> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPaymentCard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPaymentCard(PathMetadata metadata, PathInits inits) {
        this(PaymentCard.class, metadata, inits);
    }

    public QPaymentCard(Class<? extends PaymentCard> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.payment = inits.isInitialized("payment") ? new QPayment(forProperty("payment"), inits.get("payment")) : null;
    }

}

