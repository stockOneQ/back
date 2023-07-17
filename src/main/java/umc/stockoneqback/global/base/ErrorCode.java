package umc.stockoneqback.global.base;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();
    String getErrorCode();
    String getMessage();
}
