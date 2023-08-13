package umc.stockoneqback.role.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.global.base.Status;
import umc.stockoneqback.role.domain.store.PartTimer;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.exception.StoreErrorCode;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.ANNE;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("Store [Service Layer] -> StoreService 테스트")
class StoreServiceTest extends ServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private StoreService storeService;

    @Nested
    @DisplayName("가게 생성")
    class save {
        @Test
        @DisplayName("이미 있는 가게 이름이면 가게 생성에 실패한다")
        void throwExceptionByAlreadyExistStore() {
            // given
            storeService.save("스타벅스 - 광화문점", "카페", "서울시 종로구");

            // when - then
            assertThatThrownBy(() -> storeService.save("스타벅스 - 광화문점", "카페", "서울시 종로구"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(StoreErrorCode.ALREADY_EXIST_STORE.getMessage());
        }

        @Test
        @DisplayName("가게 생성에 성공한다")
        void success() {
            // when
            Long storeId1 = storeService.save("스타벅스 - 광화문점", "카페", "서울시 종로구");
            Long storeId2 = storeService.save("스타벅스 - 을지로점", "카페", "서울시 중구");

            // then
            Store findStore1 = storeRepository.findById(storeId1).orElseThrow();
            Store findStore2 = storeRepository.findById(storeId2).orElseThrow();
            assertAll(
                    () -> assertThat(findStore1.getName()).isEqualTo("스타벅스 - 광화문점"),
                    () -> assertThat(findStore1.getSector()).isEqualTo("카페"),
                    () -> assertThat(findStore1.getAddress()).isEqualTo("서울시 종로구"),
                    () -> assertThat(findStore1.getStatus()).isEqualTo(Status.NORMAL),
                    () -> assertThat(findStore2.getName()).isEqualTo("스타벅스 - 을지로점"),
                    () -> assertThat(findStore2.getSector()).isEqualTo("카페"),
                    () -> assertThat(findStore2.getAddress()).isEqualTo("서울시 중구"),
                    () -> assertThat(findStore2.getStatus()).isEqualTo(Status.NORMAL)
            );
        }
    }

    @Test
    @DisplayName("가게 ID로 가게를 조회한다")
    void findById() {
        // given
        Store store1 = createStore("스타벅스 - 광화문점", "카페", "ABC123", "서울시 종로구");
        Store store2 = createStore("스타벅스 - 을지로점", "카페", "ABC123", "서울시 중구");
        Store store3 = createStore("스타벅스 - 신촌점", "카페", "ABC123", "서울시 서대문구");

        Long storeId1 = storeRepository.save(store1).getId();
        Long storeId2 = storeRepository.save(store2).getId();
        Long storeId3 = storeRepository.save(store3).getId();

        // when
        Store findStore1 = storeService.findById(storeId1);
        Store findStore2 = storeService.findById(storeId2);
        Store findStore3 = storeService.findById(storeId3);

        // then
        assertAll(
                () -> assertThat(findStore1).isEqualTo(store1),
                () -> assertThat(findStore2).isEqualTo(store2),
                () -> assertThat(findStore3).isEqualTo(store3)
        );

        assertThatThrownBy(() -> storeService.findById(storeId1+100L))
                .isInstanceOf(BaseException.class)
                .hasMessage(StoreErrorCode.STORE_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> storeService.findById(storeId2+100L))
                .isInstanceOf(BaseException.class)
                .hasMessage(StoreErrorCode.STORE_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> storeService.findById(storeId3+100L))
                .isInstanceOf(BaseException.class)
                .hasMessage(StoreErrorCode.STORE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("가게 이름으로 가게를 조회한다")
    void findByName() {
        // given
        Store store1 = createStore("스타벅스 - 광화문점", "카페", "ABC123", "서울시 종로구");
        Store store2 = createStore("스타벅스 - 을지로점", "카페", "ABC123", "서울시 중구");
        Store store3 = createStore("스타벅스 - 신촌점", "카페", "ABC123", "서울시 서대문구");

        storeRepository.save(store1);
        storeRepository.save(store2);
        storeRepository.save(store3);

        // when
        Store findStore1 = storeService.findByName("스타벅스 - 광화문점");
        Store findStore2 = storeService.findByName("스타벅스 - 을지로점");
        Store findStore3 = storeService.findByName("스타벅스 - 신촌점");

        // then
        assertAll(
                () -> assertThat(findStore1).isEqualTo(store1),
                () -> assertThat(findStore2).isEqualTo(store2),
                () -> assertThat(findStore3).isEqualTo(store3)
        );

        assertThatThrownBy(() -> storeService.findByName("투썸플레이스 - 광화문점"))
                .isInstanceOf(BaseException.class)
                .hasMessage(StoreErrorCode.STORE_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> storeService.findByName("투썸플레이스 - 을지로점"))
                .isInstanceOf(BaseException.class)
                .hasMessage(StoreErrorCode.STORE_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> storeService.findByName("투썸플레이스 - 신촌점"))
                .isInstanceOf(BaseException.class)
                .hasMessage(StoreErrorCode.STORE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("가게 사장님 삭제에 성공한다")
    void deleteManager() {
        // given
        Long storeId = storeService.save("스타벅스 - 광화문점", "카페", "서울시 종로구");
        userService.saveManager(ANNE.toUser(), storeId);

        // when
        Store store = storeRepository.findById(storeId).orElseThrow();
        storeService.deleteManager(store);

        // then
        assertThat(store.getManager()).isNull();
    }

    @Test
    @DisplayName("가게 아르바이생님 삭제에 성공한다")
    void deletePartTimersByPartTimer() {
        // given
        Long storeId = storeService.save("스타벅스 - 광화문점", "카페", "서울시 종로구");
        Store store = storeRepository.findById(storeId).orElseThrow();

        Long userId = userService.savePartTimer(SAEWOO.toUser(), store.getName(), store.getCode());
        User user = userRepository.findById(userId).orElseThrow();
        PartTimer partTimer = partTimerRepository.findByPartTimer(user).orElseThrow();

        // when
        storeService.deletePartTimersByPartTimer(store, partTimer);

        // then
        assertThat(store.getPartTimers().getPartTimers().contains(partTimer)).isFalse();
    }

    private Store createStore(String name, String sector, String code, String address) {
        return Store.builder()
                .name(name)
                .sector(sector)
                .code(code)
                .address(address)
                .build();
    }
}