package umc.stockoneqback.business.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorCode implements ErrorCode {
    ALREADY_EXIST_BUSINESS(HttpStatus.CONFLICT, "BUSINESS_001", "이미 연결된 슈퍼바이저 - 점주 관계입니다."),
    BUSINESS_NOT_FOUND(HttpStatus.NOT_FOUND, "BUSINESS_002", "연결된 슈퍼바이저 - 점주 관계를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
