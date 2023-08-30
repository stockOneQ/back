package umc.stockoneqback.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.board.exception.BoardErrorCode;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.global.utils.EnumStandard;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum BoardSortCondition implements EnumStandard {
    TIME("최신순"),
    HIT("조회순");

    private final String value;

    public static BoardSortCondition from(String value) {
        return Arrays.stream(values())
                .filter(boardSortCondition -> boardSortCondition.value.equals(value))
                .findFirst()
                .orElseThrow(() -> BaseException.type(BoardErrorCode.NOT_FOUND_SORT_CONDITION));
    }
}
