package umc.stockoneqback.auth.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.common.EmbeddedRedisConfig;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataRedisTest
@Import(EmbeddedRedisConfig.class)
@DisplayName("Auth [Repository Layer] -> RefreshTokenRedisRepository 테스트")
class RefreshTokenRedisRepositoryTest {
    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    private final Long USER_ID = 1L;
    private final String REFRESH_TOKEN = "example_refresh_token";

    @BeforeEach
    void setup() {
        refreshTokenRedisRepository.save(RefreshToken.createRefreshToken(USER_ID, REFRESH_TOKEN));
    }

    @Test
    @DisplayName("사용자 id를 통해서 보유하고 있는 RefreshToken을 조회한다")
    void findByMemberId() {
        // when
        Optional<RefreshToken> findToken = refreshTokenRedisRepository.findById(USER_ID);

        // then
        assertThat(findToken).isPresent();
        assertAll(
                () -> {
                    RefreshToken refreshToken = findToken.get();
                    assertThat(refreshToken.getId()).isEqualTo(USER_ID);
                    assertThat(refreshToken.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
                }
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteByMemberId() {
        // when
        refreshTokenRedisRepository.deleteById(USER_ID);

        // then
        Optional<RefreshToken> findToken = refreshTokenRedisRepository.findById(USER_ID);
        assertThat(findToken).isEmpty();
    }
}