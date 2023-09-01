package umc.stockoneqback.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum BoardSearchType implements EnumStandard {
    TITLE("제목"),
    CONTENT("내용"),
    WRITER("작성자");

    private final String value;

    public static BoardSearchType from(String value) {
        return Arrays.stream(values())
                .filter(boardSearchType -> boardSearchType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> BaseException.type(BoardErrorCode.NOT_FOUND_SEARCH_TYPE));
    }
}