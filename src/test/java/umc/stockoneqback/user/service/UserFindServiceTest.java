package umc.stockoneqback.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.domain.FriendStatus;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.controller.dto.response.FindManagerResponse;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.infra.query.dto.FindManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.*;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("User [Service Layer] -> UserFindService 테스트")
class UserFindServiceTest extends ServiceTest {
    @Autowired
    private UserFindService userFindService;

    private final Store[] storeList = new Store[10];
    private final User[] userList = new User[10];
    private static final String CONDITION_NAME = "이름";
    private static final String CONDITION_STORE = "상호명";
    private static final String CONDITION_ADDRESS = "지역명";
    private static final String SEARCH_NAME = "노운";
    private static final String SEARCH_STORE = "빵";
    private static final String SEARCH_ADDRESS = "영통구";
    private static final int PAGE_SIZE = 6;

    @BeforeEach
    void setUp() {
        storeList[0] = storeRepository.save(Z_YEONGTONG.toStore());
        storeList[1] = storeRepository.save(Z_SIHEUNG.toStore());
        storeList[2] = storeRepository.save(Y_YEONGTONG.toStore());
        storeList[3] = storeRepository.save(A_PASTA.toStore());
        storeList[4] = storeRepository.save(B_CHICKEN.toStore());

        userList[0] = userRepository.save(ANNE.toUser());
        userList[1] = userRepository.save(ELLA.toUser());
        userList[2] = userRepository.save(MIKE.toUser());
        userList[3] = userRepository.save(SOPHIA.toUser());
        userList[4] = userRepository.save(UNKNOWN.toUser());

        for (int i = 0; i < 5; i++) {
            storeList[i].updateStoreManager(userList[i]);
        }
    }

    @Test
    @DisplayName("검색 조건과 검색 값에 따라 매니저를 검색한다 - 이름으로 친구 찾기")
    void findManagerByName() {
        // given
        friendRepository.save(Friend.createFriend(userList[0], userList[1], FriendStatus.ACCEPT));

        // when
        FindManagerResponse findManagerResponse = userFindService.findManager(
                userList[0].getId(), CONDITION_NAME, Long.valueOf(-1), SEARCH_NAME);

        // then
        assertThat(findManagerResponse.searchedUser().size()).isLessThanOrEqualTo(PAGE_SIZE);

        FindManager findManager = findManagerResponse.searchedUser().get(0);
        assertAll(
                () -> assertThat(findManager.getId()).isEqualTo(userList[4].getId()),
                () -> assertThat(findManager.getName()).isEqualTo(userList[4].getName()),
                () -> assertThat(findManager.getStoreName()).isEqualTo(userList[4].getManagerStore().getName()),
                () -> assertThat(findManager.getPhoneNumber()).isEqualTo(userList[4].getPhoneNumber())
        );
    }

    @Test
    @DisplayName("검색 조건과 검색 값에 따라 매니저를 검색한다 - 상호명으로 친구 찾기")
    void findManagerByStore() {
        // given
        friendRepository.save(Friend.createFriend(userList[0], userList[1], FriendStatus.ACCEPT));

        // when
        FindManagerResponse findManagerResponse = userFindService.findManager(
                userList[0].getId(), CONDITION_STORE, Long.valueOf(-1), SEARCH_STORE);

        // then
        assertThat(findManagerResponse.searchedUser().size()).isLessThanOrEqualTo(PAGE_SIZE);
        assertThat(findManagerResponse.searchedUser().size()).isEqualTo(1);

        FindManager findManager = findManagerResponse.searchedUser().get(0);
        assertAll(
                () -> assertThat(findManager.getId()).isEqualTo(userList[2].getId()),
                () -> assertThat(findManager.getName()).isEqualTo(userList[2].getName()),
                () -> assertThat(findManager.getStoreName()).isEqualTo(userList[2].getManagerStore().getName()),
                () -> assertThat(findManager.getPhoneNumber()).isEqualTo(userList[2].getPhoneNumber())
        );
    }

    @Test
    @DisplayName("검색 조건과 검색 값에 따라 매니저를 검색한다 - 지역명으로 친구 찾기")
    void findManagerByAddress() {
        // given
        friendRepository.save(Friend.createFriend(userList[0], userList[1], FriendStatus.ACCEPT));

        // when
        FindManagerResponse findManagerResponse = userFindService.findManager(
                userList[0].getId(), CONDITION_ADDRESS, Long.valueOf(-1), SEARCH_ADDRESS);

        // then
        assertThat(findManagerResponse.searchedUser().size()).isLessThanOrEqualTo(PAGE_SIZE);
        assertThat(findManagerResponse.searchedUser().size()).isEqualTo(1);

        FindManager findManager = findManagerResponse.searchedUser().get(0);
        assertAll(
                () -> assertThat(findManager.getId()).isEqualTo(userList[2].getId()),
                () -> assertThat(findManager.getName()).isEqualTo(userList[2].getName()),
                () -> assertThat(findManager.getStoreName()).isEqualTo(userList[2].getManagerStore().getName()),
                () -> assertThat(findManager.getPhoneNumber()).isEqualTo(userList[2].getPhoneNumber())
        );
    }
}