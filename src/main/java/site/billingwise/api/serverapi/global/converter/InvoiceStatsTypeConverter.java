package site.billingwise.api.serverapi.global.converter;

import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import site.billingwise.api.serverapi.domain.stats.InvoiceStatsType;

@Converter
@Slf4j
public class InvoiceStatsTypeConverter extends AbstractConverter<InvoiceStatsType> {
    public InvoiceStatsTypeConverter() {
        super(InvoiceStatsType.class);
    }
}
