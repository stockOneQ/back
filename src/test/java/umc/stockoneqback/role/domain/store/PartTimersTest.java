package umc.stockoneqback.role.domain.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.fixture.StoreFixture;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("PartTimers 도메인 테스트")
class PartTimersTest {
    private Store store;

    private User partTimer1;
    private User partTimer2;

    @BeforeEach
    void setUp() {
        User manager = ANNE.toUser();
        partTimer1 = SAEWOO.toUser();
        partTimer2 = BOB.toUser();

        store = StoreFixture.A_PASTA.toStore(manager);
    }

    @Test
    @DisplayName("PartTimer를 추가한다")
    void addPartTimer() {
        // given
        PartTimers partTimers = PartTimers.createPartTimers();

        // when
        partTimers.addPartTimer(PartTimer.createPartTimer(store, partTimer1));
        partTimers.addPartTimer(PartTimer.createPartTimer(store, partTimer2));

        // then
        assertAll(
                () -> assertThat(partTimers.getPartTimers()).hasSize(2),
                () -> assertThat(partTimers.getPartTimers())
                        .map(PartTimer::getPartTimer)
                        .contains(partTimer1, partTimer2)
        );
    }

    @Test
    @DisplayName("PartTimer를 삭제한다")
    void deletePartTimer() {
        // given
        PartTimers partTimers = PartTimers.createPartTimers();
        PartTimer partTimer1 = PartTimer.createPartTimer(store, this.partTimer1);
        PartTimer partTimer2 = PartTimer.createPartTimer(store, this.partTimer2);
        partTimers.addPartTimer(partTimer1);
        partTimers.addPartTimer(partTimer2);

        // when
        partTimers.deletePartTimer(partTimer1);

        // then
        assertAll(
                () -> assertThat(partTimers.getPartTimers()).hasSize(1),
                () -> assertThat(partTimers.getPartTimers()).doesNotContain(partTimer1),
                () -> assertThat(partTimers.getPartTimers()).contains(partTimer2)
        );
    }
}