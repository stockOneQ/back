package umc.stockoneqback.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SearchType implements EnumStandard {
    TITLE("제목"),
    CONTENT("내용"),
    WRITER("작성자");

    private String value;

    private static final Map<String, SearchType> map = new HashMap<>();
    static {
        for (SearchType searchType : values()) {
            map.put(searchType.value, searchType);
        }
    }

    public static SearchType findSearchTypeByValue(String searchTypeValue) {
        if (map.containsKey(searchTypeValue)) {
            return map.get(searchTypeValue);
        } else {
            throw BaseException.type(BoardErrorCode.NOT_FOUND_SEARCH_TYPE);
        }
    }
}