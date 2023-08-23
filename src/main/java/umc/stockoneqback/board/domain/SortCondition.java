package umc.stockoneqback.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SortCondition implements EnumStandard {
    TIME("최신순"),
    HIT("조회순");

    private String value;

    private static final Map<String, SortCondition> map = new HashMap<>();
    static {
        for (SortCondition sortCondition : values()) {
            map.put(sortCondition.value, sortCondition);
        }
    }

    public static SortCondition findSortConditionByValue(String sortConditionValue) {
        if (map.containsKey(sortConditionValue)) {
            return map.get(sortConditionValue);
        } else {
            throw BaseException.type(BoardErrorCode.NOT_FOUND_SORT_CONDITION);
        }
    }
}
