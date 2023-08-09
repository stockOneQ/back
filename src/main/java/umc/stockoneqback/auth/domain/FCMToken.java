package umc.stockoneqback.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash("FCMToken")
@AllArgsConstructor
@Builder
public class FCMToken {
    public static final Long DEFAULT_TTL = 604800L;

    @Id
    private Long id;

    @Indexed
    private String token;

    @TimeToLive
    private Long expiration;

    public static FCMToken createFCMToken(Long userId, String token) {
        return FCMToken.builder()
                .id(userId)
                .token(token)
                .expiration(DEFAULT_TTL)
                .build();
    }
}
