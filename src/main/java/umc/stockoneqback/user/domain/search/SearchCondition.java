package umc.stockoneqback.user.domain.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SearchCondition implements EnumStandard {
    NAME("이름"),
    STORE("상호명"),
    ADDRESS("지역명");

    private String value;

    private static final Map<String, SearchCondition> map = new HashMap<>();
    static {
        for (SearchCondition searchCondition : values()) {
            map.put(searchCondition.value, searchCondition);
        }
    }

    public static SearchCondition getSearchConditionByValue(String searchCondition) {
        try {
            return map.get(searchCondition);
        } catch (NullPointerException exception) {
            throw BaseException.type(UserErrorCode.INVALID_SEARCH_CONDITION);
        }
    }
}
