package site.billingwise.api.serverapi.global.converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;
import site.billingwise.api.serverapi.domain.common.EnumField;
import site.billingwise.api.serverapi.global.util.EnumUtil;

@Getter
public class AbstractConverter<T extends Enum<T> & EnumField> implements AttributeConverter<T, Long> {

    private Class<T> targetEnumClass;

    public AbstractConverter(Class<T> targetEnumClass) {
        this.targetEnumClass = targetEnumClass;
    }

    @Override
    public Long convertToDatabaseColumn(T attribute) {
        return EnumUtil.toId(attribute);
    }

    @Override
    public T convertToEntityAttribute(Long id) {
        return EnumUtil.toEnum(targetEnumClass, id);
    }
}
