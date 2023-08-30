package umc.stockoneqback.share.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;
import umc.stockoneqback.share.exception.ShareErrorCode;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ShareSearchType implements EnumStandard {
    TITLE("제목"),
    CONTENT("내용"),
    ;

    private final String value;

    public static ShareSearchType from(String value) {
        return Arrays.stream(values())
                .filter(shareSearchType -> shareSearchType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> BaseException.type(ShareErrorCode.NOT_FOUND_SEARCH_TYPE));
    }
}