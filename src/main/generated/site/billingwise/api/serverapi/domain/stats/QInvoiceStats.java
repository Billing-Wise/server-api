package site.billingwise.api.serverapi.domain.stats;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInvoiceStats is a Querydsl query type for InvoiceStats
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInvoiceStats extends EntityPathBase<InvoiceStats> {

    private static final long serialVersionUID = 638490730L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInvoiceStats invoiceStats = new QInvoiceStats("invoiceStats");

    public final site.billingwise.api.serverapi.domain.common.QBaseEntity _super = new site.billingwise.api.serverapi.domain.common.QBaseEntity(this);

    public final site.billingwise.api.serverapi.domain.user.QClient client;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> date = createDateTime("date", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final NumberPath<Integer> month = createNumber("month", Integer.class);

    public final NumberPath<Long> outstanding = createNumber("outstanding", Long.class);

    public final NumberPath<Long> totalCollected = createNumber("totalCollected", Long.class);

    public final NumberPath<Long> totalInvoiced = createNumber("totalInvoiced", Long.class);

    public final EnumPath<InvoiceStatsType> type = createEnum("type", InvoiceStatsType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Integer> week = createNumber("week", Integer.class);

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QInvoiceStats(String variable) {
        this(InvoiceStats.class, forVariable(variable), INITS);
    }

    public QInvoiceStats(Path<? extends InvoiceStats> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInvoiceStats(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInvoiceStats(PathMetadata metadata, PathInits inits) {
        this(InvoiceStats.class, metadata, inits);
    }

    public QInvoiceStats(Class<? extends InvoiceStats> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.client = inits.isInitialized("client") ? new site.billingwise.api.serverapi.domain.user.QClient(forProperty("client")) : null;
    }

}

