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
    INVALID_STORE_CODE(HttpStatus.BAD_REQUEST, "USER_004", "가게 코드가 맞지 않습니다."),
    INVALID_COMPANY_CODE(HttpStatus.BAD_REQUEST, "USER_005", "회사 코드가 맞지 않습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "USER_006", "중복된 로그인 아이디가 존재합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_007", "해당 회원을 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
