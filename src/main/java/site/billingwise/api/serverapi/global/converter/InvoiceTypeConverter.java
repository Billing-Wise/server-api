package site.billingwise.api.serverapi.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;

@Converter
@Slf4j
public class InvoiceTypeConverter extends AbstractConverter<InvoiceType> {

    public InvoiceTypeConverter() {
        super(InvoiceType.class);
    }
}