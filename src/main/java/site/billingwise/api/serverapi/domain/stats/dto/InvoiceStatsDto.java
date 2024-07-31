package site.billingwise.api.serverapi.domain.stats.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.billingwise.api.serverapi.domain.stats.InvoiceStatsType;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceStatsDto {

    private Long id;
    private LocalDateTime date;
    private Long totalInvoiced;
    private Long totalCollected;
    private Long outstanding;
    private Integer year;
    private Integer month;
    private Integer week;
    private InvoiceStatsType type;
}
