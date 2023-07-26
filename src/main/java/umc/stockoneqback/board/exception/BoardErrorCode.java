package umc.stockoneqback.board.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_001", "게시글 정보를 찾을 수 없습니다."),
    USER_IS_NOT_BOARD_WRITER(HttpStatus.CONFLICT, "BOARD_002", "게시글 작성자가 아닙니다."),
    ALREADY_BOARD_LIKE(HttpStatus.CONFLICT, "BOARD_003","이미 좋아요를 누른 게시글입니다."),
    SELF_BOARD_LIKE_NOT_ALLOWED(HttpStatus.CONFLICT, "BOARD_004", "본인 게시글은 좋아요를 누를 수 없습니다."),
    BOARD_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD_005", "좋아요를 누르지 않은 게시글은 좋아요 취소를 할 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
