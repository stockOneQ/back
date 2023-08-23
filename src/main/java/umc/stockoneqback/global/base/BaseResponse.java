package umc.stockoneqback.global.base;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.exception.ErrorCode;

import static umc.stockoneqback.global.exception.GlobalErrorCode.SUCCESS;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"status", "errorCode", "message", "result"})
public class BaseResponse<T> { // BaseResponse 객체를 사용할때 성공, 실패 경우
    private final HttpStatus status;
    private final String errorCode;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 요청에 성공한 경우
    public BaseResponse(T result) {
        this.status = SUCCESS.getStatus();
        this.errorCode = SUCCESS.getErrorCode();
        this.message = SUCCESS.getMessage();
        this.result = result;
    }

    // 요청에 성공한 경우 (status를 추가로 받는 경우)
    public BaseResponse(ErrorCode status, T result) {
        this.status = status.getStatus();
        this.errorCode = status.getErrorCode();
        this.message = status.getMessage();
        this.result = result;
    }

    // 요청에 성공했지만 result가 주어지지 않았거나 요청에 실패한 경우
    public BaseResponse(ErrorCode status) {
        this.status = status.getStatus();
        this.errorCode = status.getErrorCode();
        this.message = status.getMessage();
    }
}