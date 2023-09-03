package umc.stockoneqback.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.auth.domain.FcmToken;
import umc.stockoneqback.auth.domain.Token;
import umc.stockoneqback.common.ServiceTest;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.TokenFixture.FCM_TOKEN;

@DisplayName("Auth [Service Layer] -> TokenService 테스트")
class TokenServiceTest extends ServiceTest {
    @Autowired
    private TokenService tokenService;

    private final Long USER_ID = 1L;
    private final String REFRESH_TOKEN = "example_refresh_token";

    @Nested
    @DisplayName("Refresh Token 발급 혹은 재발급")
    class synchronizeRefreshToken {
        @Test
        @DisplayName("RefreshToken을 보유하지 않은 사용자에게 새로운 RefreshToken을 발급한다")
        void newUser() {
            // when
            tokenService.synchronizeRefreshToken(USER_ID, REFRESH_TOKEN);

            // then
            Token findToken = tokenRepository.findByUserId(USER_ID).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
        }

        @Test
        @DisplayName("RefreshToken을 보유하고 있는 사용자에게 RefreshToken을 업데이트한다")
        void oldUser() {
            // given
            tokenRepository.save(Token.createToken(USER_ID, REFRESH_TOKEN));

            // when
            String newRefreshToken = REFRESH_TOKEN + "new";
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
        tokenRepository.save(Token.createToken(USER_ID, REFRESH_TOKEN));

        // when
        final String newRefreshToken = REFRESH_TOKEN + "_new";
        tokenService.reissueRefreshTokenByRtrPolicy(USER_ID, newRefreshToken);

        // then
        Token findToken = tokenRepository.findByUserId(USER_ID).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteRefreshTokenByMemberId() {
        // given
        tokenRepository.save(Token.createToken(USER_ID, REFRESH_TOKEN));

        // when
        tokenService.deleteRefreshTokenByMemberId(USER_ID);

        // then
        assertThat(tokenRepository.findByUserId(USER_ID)).isEmpty();
    }

    @Test
    @DisplayName("해당 RefreshToken을 사용자가 보유하고 있는지 확인한다")
    void isRefreshTokenExists() {
        // given
        tokenRepository.save(Token.createToken(USER_ID, REFRESH_TOKEN));

        // when
        final String fakeRefreshToken = REFRESH_TOKEN + "_fake";
        boolean actual1 = tokenService.isRefreshTokenExists(USER_ID, REFRESH_TOKEN);
        boolean actual2 = tokenService.isRefreshTokenExists(USER_ID, fakeRefreshToken);

        // then
        assertAll(
                () -> {
                    assertThat(actual1).isTrue();
                    assertThat(actual2).isFalse();
                }
        );
    }

    @Test
    @DisplayName("현재 저장된 사용자의 FCM Token 정보를 갱신한다")
    void saveFcmToken() {
        // given
        fcmTokenRedisRepository.save(FcmToken.createFcmToken(USER_ID, FCM_TOKEN));

        // when
        final String newFcmToken = FCM_TOKEN + "_new";
        tokenService.saveFcmToken(USER_ID, newFcmToken);

        // then
        FcmToken findToken = fcmTokenRedisRepository.findById(USER_ID).orElseThrow();
        assertThat(findToken.getToken()).isEqualTo(newFcmToken);
    }

    @Test
    @DisplayName("현재 저장된 사용자의 FCM Token 정보를 삭제한다")
    void deleteFcmToken() {
        // given
        fcmTokenRedisRepository.save(FcmToken.createFcmToken(USER_ID, FCM_TOKEN));

        // when
        tokenService.deleteFcmToken(USER_ID);

        // then
        assertThat(fcmTokenRedisRepository.findById(USER_ID)).isEmpty();
    }

    @Test
    @DisplayName("현재 접속중인 모든 사용자의 FCM Token 정보를 불러온다")
    void findAllOnlineUsers() {
        // given
        final Long SECOND_USER_ID = 2L;
        final String SECOND_USER_FCM_TOKEN = "example_refresh_token_2";
        fcmTokenRedisRepository.save(FcmToken.createFcmToken(USER_ID, FCM_TOKEN));
        fcmTokenRedisRepository.save(FcmToken.createFcmToken(SECOND_USER_ID, SECOND_USER_FCM_TOKEN));

        // when
        List<FcmToken> fcmTokenList = tokenService.findAllOnlineUsers();
        fcmTokenList.sort(Comparator.comparing(FcmToken::getId));

        // then
        assertAll(
                () -> assertThat(fcmTokenList.size()).isEqualTo(2),
                () -> assertThat(fcmTokenList.get(0).getId()).isEqualTo(USER_ID),
                () -> assertThat(fcmTokenList.get(0).getToken()).isEqualTo(FCM_TOKEN),
                () -> assertThat(fcmTokenList.get(1).getId()).isEqualTo(SECOND_USER_ID),
                () -> assertThat(fcmTokenList.get(1).getToken()).isEqualTo(SECOND_USER_FCM_TOKEN)
        );
    }
}