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
    USER_IS_NOT_MANAGER(HttpStatus.BAD_REQUEST, "USER_008", "해당 유저는 사장님이 아닙니다."),
    USER_IS_NOT_ALLOWED_TO_SEARCH(HttpStatus.BAD_REQUEST, "USER_009", "해당 유저는 점주 검색 권한이 없습니다."),
    USER_STORE_MATCH_FAIL(HttpStatus.BAD_REQUEST, "USER_010", "요청한 사용자가 요청한 가게 소속이 아닙니다."),
    USER_PRODUCT_MATCH_FAIL(HttpStatus.BAD_REQUEST, "USER_011", "요청한 사용자가 요청한 제품을 가지고 있지 않습니다."),
    INVALID_SEARCH_CONDITION(HttpStatus.NOT_FOUND, "USER_012", "유효하지 않은 검색 조건입니다.")
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
