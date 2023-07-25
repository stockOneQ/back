package umc.stockoneqback.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "토큰의 유효기간이 만료되었습니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "토큰이 유효하지 않습니다."),
    INVALID_PERMISSION(HttpStatus.FORBIDDEN, "AUTH_003", "권한이 없습니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH_004", "비밀번호가 일치하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
