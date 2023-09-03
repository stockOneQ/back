package umc.stockoneqback.auth.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Repository Layer] -> TokenRepository 테스트")
class TokenRepositoryTest extends RepositoryTest {
    @Autowired
    private TokenRepository tokenRepository;

    private final Long USER_ID = 1L;
    private final String REFRESH_TOKEN = "example_refresh_token";

    @BeforeEach
    void setup() {
        tokenRepository.save(Token.createToken(USER_ID, REFRESH_TOKEN));
    }

    @Test
    @DisplayName("사용자 id를 통해서 보유하고 있는 RefreshToken을 조회한다")
    void findByMemberId() {
        // when
        Optional<Token> findToken = tokenRepository.findByUserId(USER_ID);

        // then
        assertThat(findToken).isPresent();
        assertAll(
                () -> {
                    Token token = findToken.get();
                    assertThat(token.getUserId()).isEqualTo(USER_ID);
                    assertThat(token.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
                }
        );
    }

    @Test
    @DisplayName("RTR 정책에 의해서 사용자가 보유하고 있는 RefreshToken을 재발급한다")
    void reissueRefreshTokenByRtrPolicy() {
        // when
        final String newRefreshToken = REFRESH_TOKEN + "_reissue";
        tokenRepository.reissueRefreshTokenByRtrPolicy(USER_ID, newRefreshToken);

        // then
        Token findToken = tokenRepository.findByUserId(USER_ID).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void existsByMemberIdAndRefreshToken() {
        // when
        final String fakeRefreshToken = "fake_refresh_token";
        boolean actual1 = tokenRepository.existsByUserIdAndRefreshToken(USER_ID, REFRESH_TOKEN);
        boolean actual2 = tokenRepository.existsByUserIdAndRefreshToken(USER_ID, fakeRefreshToken);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteByMemberId() {
        // when
        tokenRepository.deleteByUserId(USER_ID);

        // then
        Optional<Token> findToken = tokenRepository.findByUserId(USER_ID);
        assertThat(findToken).isEmpty();
    }
}