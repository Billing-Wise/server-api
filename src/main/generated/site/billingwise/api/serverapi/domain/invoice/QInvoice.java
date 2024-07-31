package site.billingwise.api.serverapi.domain.invoice;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInvoice is a Querydsl query type for Invoice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInvoice extends EntityPathBase<Invoice> {

    private static final long serialVersionUID = 74646531L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInvoice invoice = new QInvoice("invoice");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    public final NumberPath<Long> chargeAmount = createNumber("chargeAmount", Long.class);

    public final site.billingwise.api.serverapi.domain.contract.QContract contract;

    public final DateTimePath<java.time.LocalDateTime> contractDate = createDateTime("contractDate", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> dueDate = createDateTime("dueDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<InvoiceType> invoiceType = createEnum("invoiceType", InvoiceType.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final EnumPath<PaymentStatus> paymentStatus = createEnum("paymentStatus", PaymentStatus.class);

    public final EnumPath<site.billingwise.api.serverapi.domain.contract.PaymentType> paymentType = createEnum("paymentType", site.billingwise.api.serverapi.domain.contract.PaymentType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QInvoice(String variable) {
        this(Invoice.class, forVariable(variable), INITS);
    }

    public QInvoice(Path<? extends Invoice> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInvoice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInvoice(PathMetadata metadata, PathInits inits) {
        this(Invoice.class, metadata, inits);
    }

    public QInvoice(Class<? extends Invoice> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.contract = inits.isInitialized("contract") ? new site.billingwise.api.serverapi.domain.contract.QContract(forProperty("contract"), inits.get("contract")) : null;
    }

}

