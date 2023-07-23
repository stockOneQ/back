package umc.stockoneqback.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.enumconfig.EnumConverter;
import umc.stockoneqback.global.enumconfig.EnumStandard;
import umc.stockoneqback.product.exception.ProductErrorCode;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum StoreCondition implements EnumStandard {
    FREEZING("냉동"),
    REFRIGERATING("냉장"),
    ROOM("상온");

    private String value;

    private static final Map<String, StoreCondition> map = new HashMap<>();
    static {
        for (StoreCondition storeCondition : values()) {
            map.put(storeCondition.value, storeCondition);
        }
    }

    public static StoreCondition findStoreConditionByValue(String storeConditionValue) {
        try {
            return map.get(storeConditionValue);
        } catch (NullPointerException exception) {
            throw BaseException.type(ProductErrorCode.NOT_FOUND_STORE_CONDITION);
        }
    }

    @javax.persistence.Converter
    public static class StoreConditionConverter extends EnumConverter<StoreCondition> {
        public StoreConditionConverter() {
            super(StoreCondition.class);
        }
    }
}
