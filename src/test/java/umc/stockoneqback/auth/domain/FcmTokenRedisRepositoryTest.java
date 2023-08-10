package umc.stockoneqback.auth.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.common.EmbeddedRedisConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.TokenFixture.FCM_TOKEN;

@DataRedisTest
@Import(EmbeddedRedisConfig.class)
@DisplayName("Auth [Repository Layer] -> FcmTokenRedisRepository 테스트")
public class FcmTokenRedisRepositoryTest {
    @Autowired
    private FcmTokenRedisRepository fcmTokenRedisRepository;

    final Long USER_ID = 1L;

    @BeforeEach
    void setup() {
        fcmTokenRedisRepository.save(FcmToken.createFcmToken(USER_ID, FCM_TOKEN));
    }

    @Test
    @DisplayName("현재 사용자의 FcmToken을 추가한다")
    void reissueFcmToken() {
        // when
        final Long NEW_USER_ID = 2L;
        final String NEW_FCM_TOKEN = "newfcmtoken";
        fcmTokenRedisRepository.save(FcmToken.createFcmToken(NEW_USER_ID, NEW_FCM_TOKEN));

        // then
        FcmToken findFcmToken = fcmTokenRedisRepository.findById(NEW_USER_ID).orElseThrow();
        assertThat(findFcmToken.getToken()).isEqualTo(NEW_FCM_TOKEN);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 FcmToken을 삭제한다")
    void deleteByMemberId() {
        // when
        fcmTokenRedisRepository.deleteById(USER_ID);

        // then
        Optional<FcmToken> findFcmToken = fcmTokenRedisRepository.findById(USER_ID);
        assertThat(findFcmToken).isEmpty();
    }

    @Test
    @DisplayName("저장된 모든 사용자의 FcmToken 목록을 조회한다")
    void findAll() {
        // when
        List<FcmToken> fcmTokenList = fcmTokenRedisRepository.findAll();

        // then
        assertAll(
                () -> {
                    assertThat(fcmTokenList.size()).isEqualTo(1);
                    assertThat(fcmTokenList.get(0).getId()).isEqualTo(USER_ID);
                    assertThat(fcmTokenList.get(0).getToken()).isEqualTo(FCM_TOKEN);
                }
        );
    }
}
