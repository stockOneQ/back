package umc.stockoneqback.friend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum FriendErrorCode implements ErrorCode {
    NOT_A_MANAGER(HttpStatus.BAD_REQUEST, "FRIEND_001", "친구 신청은 사장님끼리만 가능합니다."),
    IS_A_PART_TIMER(HttpStatus.BAD_REQUEST, "FRIEND_002", "친구 검색은 사장님과 슈퍼바이저만 가능합니다."),
    FRIEND_NOT_FOUND(HttpStatus.BAD_REQUEST, "FRIEND_003", "친구 검색은 사장님과 슈퍼바이저만 가능합니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
