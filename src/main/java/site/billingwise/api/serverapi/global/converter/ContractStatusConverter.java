package site.billingwise.api.serverapi.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import site.billingwise.api.serverapi.domain.contract.ContractStatus;

@Converter
@Slf4j
public class ContractStatusConverter extends AbstractConverter<ContractStatus> {

    public ContractStatusConverter() {
        super(ContractStatus.class);
    }
}
