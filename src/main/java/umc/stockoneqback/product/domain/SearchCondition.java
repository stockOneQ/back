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
public enum SearchCondition implements EnumStandard {
    ALL("전체"),
    PASS("유통기한 경과"),
    CLOSE("유통기한 임박"),
    LACK("재고 부족"),
    ;

    private String value;

    private static final Map<String, SearchCondition> map = new HashMap<>();
    static {
        for (SearchCondition searchCondition: values()) {
            map.put(searchCondition.value, searchCondition);
        }
    }

    public static SearchCondition findSearchConditionByValue(String searchConditionValue) {
        try {
            return map.get(searchConditionValue);
        } catch (NullPointerException exception) {
            throw BaseException.type(ProductErrorCode.NOT_FOUND_SEARCH_CONDITION);
        }
    }
}
