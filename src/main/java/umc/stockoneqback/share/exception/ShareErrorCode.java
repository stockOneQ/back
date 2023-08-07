package umc.stockoneqback.share.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ShareErrorCode implements ErrorCode {
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "SHARE_001", "존재하지 않는 카테고리입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}