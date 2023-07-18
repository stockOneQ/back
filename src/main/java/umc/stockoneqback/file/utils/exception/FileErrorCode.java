package umc.stockoneqback.file.utils.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "UPLOAD_001", "전송된 파일이 없습니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "UPLOAD_002", "파일 업로드에 실패했습니다."),
    INVALID_DIR(HttpStatus.BAD_REQUEST, "UPLOAD_003", "유효하지 않은 경로입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
