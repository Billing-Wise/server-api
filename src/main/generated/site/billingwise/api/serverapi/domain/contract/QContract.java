package site.billingwise.api.serverapi.domain.contract;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QContract is a Querydsl query type for Contract
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContract extends EntityPathBase<Contract> {

    private static final long serialVersionUID = -1209209979L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContract contract = new QContract("contract");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    public final NumberPath<Long> chargeAmount = createNumber("chargeAmount", Long.class);

    public final NumberPath<Integer> contractCycle = createNumber("contractCycle", Integer.class);

    public final EnumPath<ContractStatus> contractStatus = createEnum("contractStatus", ContractStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<site.billingwise.api.serverapi.domain.invoice.Invoice, site.billingwise.api.serverapi.domain.invoice.QInvoice> invoiceList = this.<site.billingwise.api.serverapi.domain.invoice.Invoice, site.billingwise.api.serverapi.domain.invoice.QInvoice>createSet("invoiceList", site.billingwise.api.serverapi.domain.invoice.Invoice.class, site.billingwise.api.serverapi.domain.invoice.QInvoice.class, PathInits.DIRECT2);

    public final EnumPath<site.billingwise.api.serverapi.domain.invoice.InvoiceType> invoiceType = createEnum("invoiceType", site.billingwise.api.serverapi.domain.invoice.InvoiceType.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final BooleanPath isEasyConsent = createBoolean("isEasyConsent");

    public final BooleanPath isSubscription = createBoolean("isSubscription");

    public final site.billingwise.api.serverapi.domain.item.QItem item;

    public final NumberPath<Integer> itemAmount = createNumber("itemAmount", Integer.class);

    public final NumberPath<Long> itemPrice = createNumber("itemPrice", Long.class);

    public final site.billingwise.api.serverapi.domain.member.QMember member;

    public final NumberPath<Integer> paymentDueCycle = createNumber("paymentDueCycle", Integer.class);

    public final EnumPath<PaymentType> paymentType = createEnum("paymentType", PaymentType.class);

    public final NumberPath<Long> totalUnpaidCount = createNumber("totalUnpaidCount", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QContract(String variable) {
        this(Contract.class, forVariable(variable), INITS);
    }

    public QContract(Path<? extends Contract> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QContract(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QContract(PathMetadata metadata, PathInits inits) {
        this(Contract.class, metadata, inits);
    }

    public QContract(Class<? extends Contract> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new site.billingwise.api.serverapi.domain.item.QItem(forProperty("item"), inits.get("item")) : null;
        this.member = inits.isInitialized("member") ? new site.billingwise.api.serverapi.domain.member.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

