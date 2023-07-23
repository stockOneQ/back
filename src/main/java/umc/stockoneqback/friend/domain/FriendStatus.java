package umc.stockoneqback.friend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.enumconfig.EnumConverter;
import umc.stockoneqback.global.enumconfig.EnumStandard;

@Getter
@AllArgsConstructor
public enum FriendStatus implements EnumStandard {
    REQUEST("친구 요청"),
    ACCEPT("친구 요청 수락"),
    REJECT("친구 요청 거절"),
    CANCEL("친구 요청 취소"),
    DELETE("친구 삭제")
    ;

    private final String value;

    @javax.persistence.Converter
    public static class Converter extends EnumConverter<FriendStatus> {
        public Converter() {
            super(FriendStatus.class);
        }
    }
}

