package site.billingwise.api.serverapi.domain.stats.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.billingwise.api.serverapi.domain.stats.InvoiceStats;
import site.billingwise.api.serverapi.domain.stats.InvoiceStatsType;
import site.billingwise.api.serverapi.domain.stats.dto.InvoiceStatsDto;
import site.billingwise.api.serverapi.domain.stats.repository.InvoiceStatsRepository;
import site.billingwise.api.serverapi.domain.user.User;
import site.billingwise.api.serverapi.global.exception.GlobalException;
import site.billingwise.api.serverapi.global.response.info.FailureInfo;
import site.billingwise.api.serverapi.global.util.EnumUtil;
import site.billingwise.api.serverapi.global.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class InvoiceStatsService {
    private final InvoiceStatsRepository invoiceStatsRepository;

    @Transactional(readOnly = true)
    public List<InvoiceStatsDto> getInvoiceStats(
            Long invoiceStatsTypeId,
            Integer year,
            Integer month) {

        User user = SecurityUtil.getCurrentUser().orElseThrow(
                () -> new GlobalException(FailureInfo.NOT_EXIST_USER));

        InvoiceStatsType invoiceStatsType = EnumUtil.toEnum(InvoiceStatsType.class, invoiceStatsTypeId);

        List<InvoiceStats> invoiceStatsList = invoiceStatsRepository.findByTypeAndClient(year, month, invoiceStatsType,
                user.getClient());
        List<InvoiceStatsDto> invoiceStatsDtoList = invoiceStatsList.stream()
                .map((stats) -> toDtoFromEntity(stats))
                .collect(Collectors.toList());

        return invoiceStatsDtoList;
    }

    public InvoiceStatsDto toDtoFromEntity(InvoiceStats invoiceStats) {
        return InvoiceStatsDto.builder()
                .id(invoiceStats.getId())
                .date(invoiceStats.getDate())
                .totalInvoiced(invoiceStats.getTotalInvoiced())
                .totalCollected(invoiceStats.getTotalCollected())
                .outstanding(invoiceStats.getOutstanding())
                .year(invoiceStats.getYear())
                .month(invoiceStats.getMonth())
                .week(invoiceStats.getWeek())
                .type(invoiceStats.getType())
                .build();
    }
}
