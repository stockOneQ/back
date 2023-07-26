package umc.stockoneqback.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.auth.domain.Token;
import umc.stockoneqback.common.ServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Service Layer] -> TokenService 테스트")
class TokenServiceTest extends ServiceTest {
    @Autowired
    private TokenService tokenService;

    final Long USER_ID = 1L;
    final String REFRESHTOKEN = "example_refresh_token";

    @Nested
    @DisplayName("Refresh Token 발급 혹은 재발급")
    class synchronizeRefreshToken {
        @Test
        @DisplayName("RefreshToken을 보유하지 않은 사용자에게 새로운 RefreshToken을 발급한다")
        void newUser() {
            // when
            tokenService.synchronizeRefreshToken(USER_ID, REFRESHTOKEN);

            // then
            Token findToken = tokenRepository.findByUserId(USER_ID).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(REFRESHTOKEN);
        }

        @Test
        @DisplayName("RefreshToken을 보유하고 있는 사용자에게 RefreshToken을 업데이트한다")
        void oldUser() {
            // given
            tokenRepository.save(Token.createToken(USER_ID, REFRESHTOKEN));

            // when
            String newRefreshToken = REFRESHTOKEN + "new";
            tokenService.synchronizeRefreshToken(USER_ID, newRefreshToken);

            // then
            Token findToken = tokenRepository.findByUserId(USER_ID).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
        }
    }

    @Test
    @DisplayName("RTR정책에 의해서 RefreshToken을 재발급한다")
    void reissueRefreshTokenByRtrPolicy() {
        // given
        tokenRepository.save(Token.createToken(USER_ID, REFRESHTOKEN));

        // when
        final String newRefreshToken = REFRESHTOKEN + "_new";
        tokenService.reissueRefreshTokenByRtrPolicy(USER_ID, newRefreshToken);

        // then
        Token findToken = tokenRepository.findByUserId(USER_ID).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteRefreshTokenByMemberId() {
        // given
        tokenRepository.save(Token.createToken(USER_ID, REFRESHTOKEN));

        // when
        tokenService.deleteRefreshTokenByMemberId(USER_ID);

        // then
        assertThat(tokenRepository.findByUserId(USER_ID)).isEmpty();
    }

    @Test
    @DisplayName("해당 RefreshToken을 사용자가 보유하고 있는지 확인한다")
    void isRefreshTokenExists() {
        // given
        tokenRepository.save(Token.createToken(USER_ID, REFRESHTOKEN));

        // when
        final String fakeRefreshToken = REFRESHTOKEN + "_fake";
        boolean actual1 = tokenService.isRefreshTokenExists(USER_ID, REFRESHTOKEN);
        boolean actual2 = tokenService.isRefreshTokenExists(USER_ID, fakeRefreshToken);

        // then
        assertAll(
                () -> {
                    assertThat(actual1).isTrue();
                    assertThat(actual2).isFalse();
                }
        );
    }
}