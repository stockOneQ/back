package umc.stockoneqback.reply.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ReplyErrorCode implements ErrorCode {
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "REPLY_001", "대댓글 정보를 찾을 수 없습니다."),
    USER_IS_NOT_REPLY_WRITER(HttpStatus.CONFLICT, "REPLY_002", "대댓글 작성자가 아닙니다.")
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
