package umc.stockoneqback.role.domain.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.A_PASTA;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("PartTimer 도메인 테스트")
class PartTimerTest {
    private Store store;
    private User user;

    @BeforeEach
    void setUp() {
        store = A_PASTA.toStore();
        user = SAEWOO.toUser();
    }

    @Test
    @DisplayName("PartTimer 생성에 성공한다")
    void createPartTimer() {
        // when
        PartTimer partTimer = PartTimer.createPartTimer(store, user);

        // then
        assertAll(
                () -> assertThat(partTimer.getPartTimer()).isEqualTo(user),
                () -> assertThat(partTimer.getStore()).isEqualTo(store)
        );
    }
}