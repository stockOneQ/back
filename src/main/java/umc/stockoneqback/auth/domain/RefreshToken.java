package umc.stockoneqback.auth.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("Token")
public class RefreshToken {
    public static final Long DEFAULT_TTL = 1209600L;

    @Id
    private Long id;

    private String refreshToken;

    @TimeToLive
    private Long expiration;

    @Builder
    public RefreshToken(Long id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.expiration = DEFAULT_TTL;
    }

    public static RefreshToken createRefreshToken(Long userId, String refreshToken) {
        return new RefreshToken(userId, refreshToken);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
