package site.billingwise.api.serverapi.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;

@Converter
@Slf4j
public class PaymentStatusConverter extends AbstractConverter<PaymentStatus> {

    public PaymentStatusConverter() {
        super(PaymentStatus.class);
    }
}