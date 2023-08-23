package umc.stockoneqback.share.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ShareErrorCode implements ErrorCode {
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "SHARE_001", "존재하지 않는 카테고리입니다."),
    SHARE_NOT_FOUND(HttpStatus.NOT_FOUND, "SHARE_002", "게시글 정보를 찾을 수 없습니다."),
    NOT_A_WRITER(HttpStatus.CONFLICT, "SHARE_003", "게시글 작성자가 아닙니다."),
    USER_NOT_ALLOWED(HttpStatus.FORBIDDEN, "SHARE_004", "해당 페이지에 접근할 수 없는 사용자입니다."),
    NOT_FOUND_SEARCH_TYPE(HttpStatus.NOT_FOUND, "SHARE_005", "지원하지 않는 검색 조건입니다."),
    NOT_FILTERED_USER(HttpStatus.NOT_FOUND, "SHARE_006", "연결되어 있지 않은 유저입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}