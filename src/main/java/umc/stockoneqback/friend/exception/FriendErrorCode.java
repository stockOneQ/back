package umc.stockoneqback.friend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import umc.stockoneqback.global.base.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum FriendErrorCode implements ErrorCode {
    ALREADY_EXIST_FRIEND(HttpStatus.CONFLICT, "FRIEND_001", "이미 존재하는 친구 관계입니다."),
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIEND_002", "친구 관계를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
