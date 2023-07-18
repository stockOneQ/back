package umc.stockoneqback.role.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {
    ALREADY_EXIST_STORE(HttpStatus.CONFLICT, "STORE_001", "이미 존재하는 가게입니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_002", "가게를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
