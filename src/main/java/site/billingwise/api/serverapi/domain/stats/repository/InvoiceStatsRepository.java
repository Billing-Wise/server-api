package site.billingwise.api.serverapi.domain.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.billingwise.api.serverapi.domain.stats.InvoiceStats;

public interface InvoiceStatsRepository extends JpaRepository<InvoiceStats, Long>, InvoiceStatsRepositoryCustom {
}