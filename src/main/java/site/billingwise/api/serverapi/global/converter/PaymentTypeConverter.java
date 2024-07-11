package site.billingwise.api.serverapi.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import site.billingwise.api.serverapi.domain.contract.PaymentType;
import site.billingwise.api.serverapi.domain.invoice.InvoiceType;
import site.billingwise.api.serverapi.domain.invoice.PaymentStatus;

@Converter
@Slf4j
public class PaymentTypeConverter extends AbstractConverter<PaymentType> {

    public PaymentTypeConverter() {
        super(PaymentType.class);
    }
}
