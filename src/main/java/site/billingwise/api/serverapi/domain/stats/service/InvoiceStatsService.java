package site.billingwise.api.serverapi.domain.stats.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.stats.InvoiceStats;
import site.billingwise.api.serverapi.domain.stats.InvoiceStatsType;
import site.billingwise.api.serverapi.domain.stats.repository.InvoiceStatsRepository;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.EnumUtil;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class InvoiceStatsService {
    final private InvoiceStatsRepository invoiceStatsRepository;

    @Transactional(readOnly = true)
    public List<InvoiceStats> getInvoiceStats(
            Integer year,
            Integer month, 
            Long invoiceStatsTypeId,
            Long clientId) {

        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        InvoiceStatsType invoiceStatsType = EnumUtil.toEnum(InvoiceStatsType.class, invoiceStatsTypeId);
        return invoiceStatsRepository.findByTypeAndClient(year, month, invoiceStatsType, user.getClient());
    }
}
