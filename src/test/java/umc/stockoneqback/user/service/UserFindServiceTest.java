package umc.stockoneqback.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.business.domain.Business;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.infra.query.dto.FindManager;
import umc.stockoneqback.user.service.dto.response.FindManagerResponse;

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
    private static final String SEARCH_TYPE_NAME = "이름";
    private static final String SEARCH_TYPE_STORE = "상호명";
    private static final String SEARCH_TYPE_ADDRESS = "지역명";
    private static final String SEARCH_NAME = "노운";
    private static final String SEARCH_STORE = "Z 과일";
    private static final String SEARCH_ADDRESS = "영통구";

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
            storeList[i].updateManager(userList[i]);
        }
    }

    @Nested
    @DisplayName("친구 찾기(매니저 검색)")
    class findFriendManagers {
        @Test
        @DisplayName("검색 조건과 검색어에 따른 매니저 검색에 성공한다")
        void success() {
            // given
            friendRepository.save(Friend.createFriend(userList[1], userList[2], RelationStatus.ACCEPT));
            friendRepository.save(Friend.createFriend(userList[0], userList[1], RelationStatus.REQUEST));

            // when
            FindManagerResponse findManagerResponse = userFindService.findFriendManagers(
                    userList[1].getId(),
                    -1L,
                    SEARCH_TYPE_ADDRESS,
                    SEARCH_ADDRESS
            );

            // then
            assertThat(findManagerResponse.searchedUser().size()).isEqualTo(2);

            FindManager findManager1 = findManagerResponse.searchedUser().get(0);
            FindManager findManager2 = findManagerResponse.searchedUser().get(1);
            assertAll(
                    () -> assertThat(findManager1.getId()).isEqualTo(userList[0].getId()),
                    () -> assertThat(findManager1.getName()).isEqualTo(userList[0].getName()),
                    () -> assertThat(findManager1.getStoreName()).isEqualTo(userList[0].getManagerStore().getName()),
                    () -> assertThat(findManager1.getPhoneNumber()).isEqualTo(userList[0].getPhoneNumber()),
                    () -> assertThat(findManager1.getRelationStatus()).isEqualTo(RelationStatus.REQUEST.getValue()),
                    () -> assertThat(findManager2.getId()).isEqualTo(userList[2].getId()),
                    () -> assertThat(findManager2.getName()).isEqualTo(userList[2].getName()),
                    () -> assertThat(findManager2.getStoreName()).isEqualTo(userList[2].getManagerStore().getName()),
                    () -> assertThat(findManager2.getPhoneNumber()).isEqualTo(userList[2].getPhoneNumber()),
                    () -> assertThat(findManager2.getRelationStatus()).isEqualTo(RelationStatus.ACCEPT.getValue())
            );
        }
    }

    @Nested
    @DisplayName("점주 찾기(매니저 검색)")
    class findBusinessManagers {
        @Test
        @DisplayName("검색 조건과 검색어에 따른 매니저 검색에 성공한다")
        void success() {
            // given
            User supervisor = userRepository.save(WIZ.toUser());
            businessRepository.save(Business.builder().manager(userList[0]).supervisor(supervisor).build());

            // when
            FindManagerResponse findManagerResponse = userFindService.findBusinessManagers(
                    supervisor.getId(), -1L, SEARCH_TYPE_STORE, SEARCH_STORE);

            // then
            FindManager findManager1 = findManagerResponse.searchedUser().get(0);
            FindManager findManager2 = findManagerResponse.searchedUser().get(1);
            assertThat(findManagerResponse.searchedUser().size()).isEqualTo(2);
            assertAll(
                    () -> assertThat(findManager1.getId()).isEqualTo(userList[0].getId()),
                    () -> assertThat(findManager1.getName()).isEqualTo(userList[0].getName()),
                    () -> assertThat(findManager1.getStoreName()).isEqualTo(userList[0].getManagerStore().getName()),
                    () -> assertThat(findManager1.getPhoneNumber()).isEqualTo(userList[0].getPhoneNumber()),
                    () -> assertThat(findManager1.getRelationStatus()).isEqualTo(RelationStatus.ACCEPT.getValue()),
                    () -> assertThat(findManager2.getId()).isEqualTo(userList[1].getId()),
                    () -> assertThat(findManager2.getName()).isEqualTo(userList[1].getName()),
                    () -> assertThat(findManager2.getStoreName()).isEqualTo(userList[1].getManagerStore().getName()),
                    () -> assertThat(findManager2.getPhoneNumber()).isEqualTo(userList[1].getPhoneNumber()),
                    () -> assertThat(findManager2.getRelationStatus()).isEqualTo(RelationStatus.IRRELEVANT.getValue())
            );
        }
    }
}