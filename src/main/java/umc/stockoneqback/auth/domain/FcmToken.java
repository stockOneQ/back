package umc.stockoneqback.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Builder
@AllArgsConstructor
@Getter
@RedisHash("FcmToken")
public class FcmToken {
    public static final Long DEFAULT_TTL = 604800L;

    @Id
    private Long id;

    @Indexed
    private String token;

    @TimeToLive
    private Long expiration;

    public static FcmToken createFcmToken(Long userId, String token) {
        return FcmToken.builder()
                .id(userId)
                .token(token)
                .expiration(DEFAULT_TTL)
                .build();
    }
}
