package umc.stockoneqback.role.domain.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Store 도메인 테스트")
class StoreTest {
    @Test
    @DisplayName("Store 생성에 성공한다")
    void createStore() {
        // when
        Store store = Store.createStore("스타벅스 - 광화문점", "카페", "서울시 중구");

        // then
        assertAll(
                () -> assertThat(store.getName()).isEqualTo("스타벅스 - 광화문점"),
                () -> assertThat(store.getSector()).isEqualTo("카페"),
                () -> assertThat(store.getAddress()).isEqualTo("서울시 중구"),
                () -> assertThat(store.getManager()).isNull(),
                () -> assertThat(store.getPartTimers().getPartTimers()).isEmpty(),
                () -> assertThat(store.getStatus()).isEqualTo(Status.NORMAL)
        );
    }

    @Test
    @DisplayName("Store Manager 등록에 성공한다")
    void updateStoreManager() {
        // given
        Store store = Store.createStore("스타벅스 - 광화문점", "카페", "서울시 중구");
        User manager = SAEWOO.toUser();

        // when
        store.updateStoreManager(manager);

        // then
        assertThat(store.getManager()).isEqualTo(manager);
    }

    @Test
    @DisplayName("Store PartTimer 등록에 성공한다")
    void updateStorePartTimers() {
        // given
        Store store = Store.createStore("스타벅스 - 광화문점", "카페", "서울시 중구");

        // when
        User user1 = SAEWOO.toUser();
        User user2 = ANNE.toUser();
        store.updateStorePartTimers(user1);
        store.updateStorePartTimers(user2);

        assertAll(
                () -> assertThat(store.getPartTimers().getPartTimers().size()).isEqualTo(2),
                () -> assertThat(store.getPartTimers().getPartTimers().get(0).getPartTimer().getName()).contains(SAEWOO.getName()),
                () -> assertThat(store.getPartTimers().getPartTimers().get(1).getPartTimer().getName()).contains(ANNE.getName())
        );
    }

    @Test
    @DisplayName("Store PartTimer 삭제에 성공한다")
    void deleteStorePartTimers() {
        // given
        Store store = Store.createStore("스타벅스 - 광화문점", "카페", "서울시 중구");
        User user1 = SAEWOO.toUser();
        User user2 = WONI.toUser();
        PartTimer partTimer1 = PartTimer.createPartTimer(store, user1);
        PartTimer partTimer2 = PartTimer.createPartTimer(store, user2);
        store.updateStorePartTimers(partTimer1);
        store.updateStorePartTimers(partTimer2);

        // when
        store.deleteStorePartTimers(partTimer1);

        // then
        assertAll(
                () -> assertThat(store.getPartTimers().getPartTimers().size()).isEqualTo(1),
                () -> assertThat(store.getPartTimers().getPartTimers().get(0).getPartTimer().getName()).contains(WONI.getName())
        );
    }

    @Test
    @DisplayName("Store Manager 삭제에 성공한다")
    void deleteStoreManager() {
        // given
        Store store = Store.createStore("스타벅스 - 광화문점", "카페", "서울시 중구");
        User user = ANNE.toUser();
        store.updateStoreManager(user);

        // when
        store.deleteStoreManager();

        // then
        assertThat(store.getManager()).isEqualTo(null);
    }
}