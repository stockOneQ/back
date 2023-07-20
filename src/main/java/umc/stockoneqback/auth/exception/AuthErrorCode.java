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
    EMAIL_AUTH_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_004", "해당 이메일에 대한 이메일 인증 정보를 찾을 수 없습니다."),
    EMAIL_AUTH_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_005", "해당 이메일에 대한 이메일 인증이 유효하지 않습니다."),
    ALREADY_EMAIL_AUTH(HttpStatus.CONFLICT, "AUTH_006", "이미 이메일 인증을 요청한 이메일입니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH_007", "비밀번호가 일치하지 않습니다."),
    EMAIL_AUTH_NOT_DONE(HttpStatus.BAD_REQUEST, "AUTH_008", "이메일 인증이 완료되지 않은 회원입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
