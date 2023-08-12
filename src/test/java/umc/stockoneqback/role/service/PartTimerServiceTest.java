package umc.stockoneqback.role.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.role.domain.store.PartTimer;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.Z_YEONGTONG;
import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

@DisplayName("PartTimer [Service Layer] -> PartTimerService 테스트")
public class PartTimerServiceTest extends ServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private PartTimerService partTimerService;

    private User user;
    private Store store;
    private PartTimer partTimer;

    @BeforeEach
    void setUp() {
        store = storeRepository.save(Z_YEONGTONG.toStore());
        user = userRepository.save(SAEWOO.toUser());
        partTimer = partTimerRepository.save(PartTimer.createPartTimer(store, user));
    }

    @Test
    @DisplayName("사용자 관련 정보를 삭제하고 사용자 개인정보의 상태를 변경한다")
    void withdrawUser() {
        // when
        partTimerService.deleteByUser(user);
        Store findStore = storeService.findById(store.getId());

        // then
        assertAll(
                () -> assertThat(partTimerRepository.findByPartTimer(user).isEmpty()).isTrue(),
                () -> assertThat(findStore.getPartTimers().getPartTimers().contains(partTimer)).isFalse()
        );
    }
}
