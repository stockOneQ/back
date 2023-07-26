package umc.stockoneqback.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "PRODUCT_001", "입력값에 해당하는 제품이 없습니다."),
    DUPLICATE_PRODUCT(HttpStatus.CONFLICT, "PRODUCT_002", "입력한 제품과 동일한 제품명이 존재합니다."),
    NOT_FOUND_STORE_CONDITION(HttpStatus.NOT_FOUND, "PRODUCT_003", "입력값에 해당하는 보관방법이 없습니다."),
    NOT_FOUND_SORT_CONDITION(HttpStatus.NOT_FOUND, "PRODUCT_004", "입력값에 해당하는 정렬 방식이 없습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
