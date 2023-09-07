package umc.stockoneqback.user.config;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import umc.stockoneqback.common.DatabaseCleaner;
import umc.stockoneqback.common.EmbeddedRedisConfig;
import umc.stockoneqback.common.RedisCleaner;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.service.UserService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.*;

@Import(EmbeddedRedisConfig.class)
@DisplayName("User [Config Layer] -> DeleteExpiredUserScheduler 테스트")
@SpringBootTest(
        properties = {
                "schedules.cron.reward.publish=0/2 * * * * ?",
        }
)
public class DeleteExpiredUserSchedulerTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private RedisCleaner redisCleaner;

    @Autowired
    private DeleteExpiredUserScheduler deleteExpiredUserScheduler;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User saewoo;
    private User woni;
    private User bob;

    @BeforeEach
    void setup() {
        databaseCleaner.execute();
        redisCleaner.flushAll();

        saewoo = userRepository.save(SAEWOO.toUser());
        woni = userRepository.save(WONI.toUser());
        bob = userRepository.save(BOB.toUser());

        userService.withdrawUser(saewoo.getId());
        userService.withdrawUser(woni.getId());

        LocalDate overTwoYears = LocalDate.now().minusYears(2);
        userRepository.updateModifiedDateById(saewoo.getId(), overTwoYears);
        userRepository.updateModifiedDateById(woni.getId(), overTwoYears);
    }

    @Nested
    @DisplayName("특정 시간마다 현재 접속중인 사용자별 유통기한 경과 제품 목록 조회")
    class getListSpecificTimeOfPassProductByOnlineUsers {
        @Test
        @DisplayName("특정 시간마다 현재 접속중인 사용자별 유통기한 경과 제품 목록 조회에 성공한다")
        void success() {
            // when
            deleteExpiredUserScheduler.deleteExpiredUser();

            // then
            Awaitility.await()
                    .atMost(3, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        Optional<User> findSaewoo = userRepository.findById(saewoo.getId());
                        Optional<User> findAnne = userRepository.findById(woni.getId());
                        Optional<User> findWiz = userRepository.findById(bob.getId());

                        assertAll(
                                () -> assertThat(findSaewoo.isEmpty()).isTrue(),
                                () -> assertThat(findAnne.isEmpty()).isTrue(),
                                () -> assertThat(findWiz.isPresent()).isTrue(),
                                () -> assertThat(findWiz.get().getId()).isEqualTo(bob.getId())
                        );
                    }
                    );
        }
    }
}
