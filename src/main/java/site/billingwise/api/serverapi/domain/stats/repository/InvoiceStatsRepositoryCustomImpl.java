package site.billingwise.api.serverapi.domain.stats.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import site.billingwise.api.serverapi.domain.stats.InvoiceStats;
import site.billingwise.api.serverapi.domain.stats.QInvoiceStats;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.stats.InvoiceStatsType;

import java.util.List;

public class InvoiceStatsRepositoryCustomImpl implements InvoiceStatsRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public List<InvoiceStats> findByTypeAndClient(Integer year, Integer month, InvoiceStatsType type, Client client) {
        QInvoiceStats invoiceStats = QInvoiceStats.invoiceStats;

        BooleanBuilder builder = new BooleanBuilder();

        if (year != null) {
            builder.and(invoiceStats.year.eq(year));
        }
        if (month != null) {
            builder.and(invoiceStats.month.eq(month));
        }
        if (type != null) {
            builder.and(invoiceStats.type.eq(type));
        }
        if (client != null) {
            builder.and(invoiceStats.client.eq(client));
        }

        return queryFactory
                .selectFrom(invoiceStats)
                .orderBy(invoiceStats.year.asc())
                .orderBy(invoiceStats.month.asc())
                .orderBy(invoiceStats.week.asc())
                .where(builder)
                .fetch();
    }
}
