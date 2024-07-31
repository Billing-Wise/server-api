package site.billingwise.api.serverapi.domain.stats.repository;

import site.billingwise.api.serverapi.domain.stats.InvoiceStats;
import site.billingwise.api.serverapi.domain.user.Client;
import site.billingwise.api.serverapi.domain.stats.InvoiceStatsType;

import java.util.List;

public interface InvoiceStatsRepositoryCustom {
    List<InvoiceStats> findByTypeAndClient(Integer year, Integer month, InvoiceStatsType type, Client client);
}
