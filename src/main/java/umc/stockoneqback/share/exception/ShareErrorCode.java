package umc.stockoneqback.share.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ShareErrorCode implements ErrorCode {
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "SHARE_001", "존재하지 않는 카테고리입니다."),
    NOT_FOUND_SEARCH_TYPE(HttpStatus.NOT_FOUND, "SHARE_002", "지원하지 않는 검색 조건입니다."),
    NOT_FILTERED_USER(HttpStatus.NOT_FOUND, "SHARE_003", "연결되어 있지 않은 유저입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}