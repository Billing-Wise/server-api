package site.billingwise.api.serverapi.global.util;

import site.billingwise.api.serverapi.domain.common.EnumField;

import java.util.EnumSet;
import java.util.NoSuchElementException;

public class EnumUtil {

    public static <T extends Enum<T> & EnumField> Long toId(T enumClass) {
        if(enumClass == null) {
            return null;
        }
        return enumClass.getId();
    }

    public static <T extends Enum<T> & EnumField> T toEnum(Class<T> enumClass, Long id) {
        if(id == null) {
            return null;
        }
        return EnumSet.allOf(enumClass).stream()
                .filter(e -> e.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException());
    }
}
