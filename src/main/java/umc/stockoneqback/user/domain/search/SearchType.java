package umc.stockoneqback.user.domain.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SearchType implements EnumStandard {
    NAME("이름"),
    STORE("상호명"),
    ADDRESS("지역명");

    private String value;

    private static final Map<String, SearchType> map = new HashMap<>();
    static {
        for (SearchType searchType : values()) {
            map.put(searchType.value, searchType);
        }
    }

    public static SearchType findFriendSearchTypeByValue(String searchTypeValue) {
        if (map.containsKey(searchTypeValue)) {
            return map.get(searchTypeValue);
        } else {
            throw BaseException.type(UserErrorCode.INVALID_SEARCH_TYPE);
        }
    }

    public static SearchType findBusinessSearchTypeByValue(String searchTypeValue) {
        if (map.get(searchTypeValue) == SearchType.NAME || map.get(searchTypeValue) == SearchType.STORE) {
            return map.get(searchTypeValue);
        } else {
            throw BaseException.type(UserErrorCode.INVALID_SEARCH_TYPE);
        }
    }
}
