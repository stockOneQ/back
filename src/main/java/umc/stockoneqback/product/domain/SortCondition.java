package umc.stockoneqback.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;
import umc.stockoneqback.product.exception.ProductErrorCode;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SortCondition implements EnumStandard {
    NAME("가나다"),
    ORDER_FREQUENCY("빈도");

    private String value;

    private static final Map<String, SortCondition> map = new HashMap<>();
    static {
        for (SortCondition sortCondition : values()) {
            map.put(sortCondition.value, sortCondition);
        }
    }

    public static SortCondition findSortConditionByValue(String sortConditionValue) {
        try {
            return map.get(sortConditionValue);
        } catch (NullPointerException exception) {
            throw BaseException.type(ProductErrorCode.NOT_FOUND_SORT_CONDITION);
        }
    }
}
