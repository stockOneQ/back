package umc.stockoneqback.auth.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("FcmToken")
public class FcmToken {
    public static final Long DEFAULT_TTL = 604800L;

    @Id
    private Long id;

    @Indexed
    private String token;

    @TimeToLive
    private Long expiration;

    @Builder
    private FcmToken(Long userId, String token) {
        this.id = userId;
        this.token = token;
        this.expiration = DEFAULT_TTL;
    }

    public static FcmToken createFcmToken(Long userId, String token) {
        return new FcmToken(userId, token);
    }
}
