package umc.stockoneqback.share.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;
import umc.stockoneqback.share.exception.ShareErrorCode;

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

    public static SearchType findShareSearchTypeByValue(String searchTypeValue) {
        if (map.get(searchTypeValue) == SearchType.CONTENT || map.get(searchTypeValue) == SearchType.TITLE) {
            return map.get(searchTypeValue);
        } else {
            throw BaseException.type(ShareErrorCode.NOT_FOUND_SEARCH_TYPE);
        }
    }
}