package umc.stockoneqback.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "USER_001", "이메일 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_PATTERN(HttpStatus.BAD_REQUEST, "USER_002", "비밀번호 형식이 올바르지 않습니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_003", "존재하지 않는 역할입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
