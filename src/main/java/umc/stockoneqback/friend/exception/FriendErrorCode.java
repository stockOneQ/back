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
    SELF_FRIEND_REQUEST_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FRIEND_003", "본인과 친구 관계를 맺을 수 없습니다."),
    STATUS_IS_ACCEPT(HttpStatus.CONFLICT, "FRIEND_004", "친구 관계가 이미 승인된 상태입니다."),
    STATUS_IS_REQUEST(HttpStatus.CONFLICT, "FRIEND_005", "친구 관계가 아직 요청 상태입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
