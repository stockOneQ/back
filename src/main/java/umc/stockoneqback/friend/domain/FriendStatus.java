package umc.stockoneqback.friend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.stockoneqback.global.utils.EnumConverter;
import umc.stockoneqback.global.utils.EnumStandard;

@Getter
@AllArgsConstructor
public enum FriendStatus implements EnumStandard {
    REQUEST("친구 요청"),
    ACCEPT("친구 수락"),
    ;

    private final String value;

    @javax.persistence.Converter
    public static class Converter extends EnumConverter<FriendStatus> {
        public Converter() {
            super(FriendStatus.class);
        }
    }
}
