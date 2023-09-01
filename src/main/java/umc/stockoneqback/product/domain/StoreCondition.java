package umc.stockoneqback.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumConverter;
import umc.stockoneqback.global.utils.EnumStandard;
import umc.stockoneqback.product.exception.ProductErrorCode;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum StoreCondition implements EnumStandard {
    FREEZING("냉동"),
    REFRIGERATING("냉장"),
    ROOM("상온");

    private final String value;

    public static StoreCondition from(String value) {
        return Arrays.stream(values())
                .filter(storeCondition -> storeCondition.value.equals(value))
                .findFirst()
                .orElseThrow(() -> BaseException.type(ProductErrorCode.NOT_FOUND_STORE_CONDITION));
    }

    @javax.persistence.Converter
    public static class StoreConditionConverter extends EnumConverter<StoreCondition> {
        public StoreConditionConverter() {
            super(StoreCondition.class);
        }
    }
}
