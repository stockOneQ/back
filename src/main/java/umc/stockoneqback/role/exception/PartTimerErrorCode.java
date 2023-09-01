package umc.stockoneqback.role.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PartTimerErrorCode implements ErrorCode {
    PART_TIMER_NOT_FOUND(HttpStatus.NOT_FOUND, "PART_TIMER_001", "알바생을 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
