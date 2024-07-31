package site.billingwise.api.serverapi.domain.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = -181208907L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPayment payment = new QPayment("payment");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final site.billingwise.api.serverapi.domain.invoice.QInvoice invoice;

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final NumberPath<Long> payAmount = createNumber("payAmount", Long.class);

    public final EnumPath<PaymentMethod> paymentMethod = createEnum("paymentMethod", PaymentMethod.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPayment(String variable) {
        this(Payment.class, forVariable(variable), INITS);
    }

    public QPayment(Path<? extends Payment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPayment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPayment(PathMetadata metadata, PathInits inits) {
        this(Payment.class, metadata, inits);
    }

    public QPayment(Class<? extends Payment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.invoice = inits.isInitialized("invoice") ? new site.billingwise.api.serverapi.domain.invoice.QInvoice(forProperty("invoice"), inits.get("invoice")) : null;
    }

}

