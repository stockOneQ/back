package umc.stockoneqback.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.enumconfig.EnumConverter;
import umc.stockoneqback.global.enumconfig.EnumStandard;

@Getter
@AllArgsConstructor
public enum StoreCondition implements EnumStandard {
    FREEZING("냉동"),
    REFRIGERATING("냉장"),
    ROOM("상온");

    private String value;

    @javax.persistence.Converter
    public static class StoreConditionConverter extends EnumConverter<StoreCondition> {
        public StoreConditionConverter() {
            super(StoreCondition.class);
        }
    }
}
