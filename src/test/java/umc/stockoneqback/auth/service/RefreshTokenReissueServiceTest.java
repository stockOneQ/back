package umc.stockoneqback.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.auth.domain.RefreshToken;
import umc.stockoneqback.auth.exception.AuthErrorCode;
import umc.stockoneqback.auth.service.dto.response.TokenResponse;
import umc.stockoneqback.auth.utils.JwtTokenProvider;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.exception.BaseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.TokenFixture.FCM_TOKEN;

@DisplayName("Auth [Service Layer] -> TokenReissueService 테스트")
class RefreshTokenReissueServiceTest extends ServiceTest {
    @Autowired
    private TokenReissueService tokenReissueService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final Long USER_ID = 1L;
    private String refreshToken;

    @BeforeEach
    void setup() {
        refreshToken = jwtTokenProvider.createRefreshToken(USER_ID);
    }

    @Nested
    @DisplayName("토큰 재발급")
    class reissueTokens {
        @Test
        @DisplayName("RefreshToken이 유효하지 않으면 예외가 발생한다")
        void throwExceptionByAuthInvalidToken() {
            // when - then
            assertThatThrownBy(() -> tokenReissueService.reissueTokens(USER_ID, refreshToken, FCM_TOKEN))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(AuthErrorCode.AUTH_INVALID_TOKEN.getMessage());
        }

        @Test
        @DisplayName("RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는데 성공한다")
        void success() {
            // given
            refreshTokenRedisRepository.save(RefreshToken.createRefreshToken(USER_ID, refreshToken));

            // when
            TokenResponse response = tokenReissueService.reissueTokens(USER_ID, refreshToken, FCM_TOKEN);

            // then
            assertAll(
                    () -> assertThat(response).isNotNull(),
                    () -> assertThat(response).usingRecursiveComparison().isNotNull()
            );
        }
    }
}