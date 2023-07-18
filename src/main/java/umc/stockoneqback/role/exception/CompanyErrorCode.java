package umc.stockoneqback.role.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CompanyErrorCode implements ErrorCode {
    ALREADY_EXIST_COMPANY(HttpStatus.CONFLICT, "COMPANY_001", "이미 존재하는 기업입니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY_002", "기업을 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
