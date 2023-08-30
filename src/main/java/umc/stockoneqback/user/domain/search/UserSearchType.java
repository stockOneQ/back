package umc.stockoneqback.user.domain.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;
import umc.stockoneqback.user.exception.UserErrorCode;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserSearchType implements EnumStandard {
    NAME("이름"),
    STORE("상호명"),
    ADDRESS("지역명");

    private final String value;

    public static UserSearchType from(String value) {
        return Arrays.stream(values())
                .filter(userSearchType -> userSearchType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> BaseException.type(UserErrorCode.INVALID_SEARCH_TYPE));
    }
}
