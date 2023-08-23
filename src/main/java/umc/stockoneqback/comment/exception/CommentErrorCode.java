package umc.stockoneqback.comment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_001", "댓글 정보를 찾을 수 없습니다."),
    USER_IS_NOT_COMMENT_WRITER(HttpStatus.CONFLICT, "COMMENT_002", "댓글 작성자가 아닙니다.")
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
