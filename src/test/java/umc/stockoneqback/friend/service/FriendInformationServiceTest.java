package umc.stockoneqback.friend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.friend.infra.query.dto.response.FriendInformation;
import umc.stockoneqback.friend.service.dto.FriendAssembler;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.*;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Friend [Service Layer] -> FriendInformationService 테스트")
class FriendInformationServiceTest extends ServiceTest {
    @Autowired
    private FriendInformationService friendInformationService;

    private final Store[] storeList = new Store[10];
    private final User[] userList = new User[10];

    private static final int FRIENDS_PAGE_SIZE = 8;
    private static final int WAITING_FRIENDS_PAGE_SIZE = 3;
    private static final int REQUESTED_FRIENDS_PAGE_SIZE = 6;

    @BeforeEach
    void setUp() {
        storeList[0] = storeRepository.save(Z_YEONGTONG.toStore());
        storeList[1] = storeRepository.save(Z_SIHEUNG.toStore());
        storeList[2] = storeRepository.save(Y_YEONGTONG.toStore());
        storeList[3] = storeRepository.save(A_PASTA.toStore());
        storeList[4] = storeRepository.save(B_CHICKEN.toStore());
        storeList[5] = storeRepository.save(C_COFFEE.toStore());
        storeList[6] = storeRepository.save(D_PIZZA.toStore());
        storeList[7] = storeRepository.save(E_CHINESE.toStore());
        storeList[8] = storeRepository.save(F_COMPANY.toStore());
        storeList[9] = storeRepository.save(G_TTEOKBOKKI.toStore());

        userList[0] = userRepository.save(SAEWOO.toUser());
        userList[1] = userRepository.save(ANNE.toUser());
        userList[2] = userRepository.save(WIZ.toUser());
        userList[3] = userRepository.save(WONI.toUser());
        userList[4] = userRepository.save(BOB.toUser());
        userList[5] = userRepository.save(ELLA.toUser());
        userList[6] = userRepository.save(JACK.toUser());
        userList[7] = userRepository.save(LILY.toUser());
        userList[8] = userRepository.save(MIKE.toUser());
        userList[9] = userRepository.save(OLIVIA.toUser());

        for (int i = 0; i < 10; i++) {
            storeList[i].updateStoreManager(userList[i]);
        }
    }

    @Test
    @DisplayName("User ID에 대하여 해당 유저의 연결된 친구 리스트를 조회한다 (lastUserId 이후 + LastModifiedDate 최신순)")
    void getFriends() {
        // given
        for (int i = 9; i >= 5; i--) {
            friendRepository.save(Friend.createFriend(userList[0], userList[i], RelationStatus.ACCEPT));
        }
        for (int i = 4; i >= 1; i--) {
            friendRepository.save(Friend.createFriend(userList[i], userList[0], RelationStatus.ACCEPT));
        }

        // when - then
        FriendAssembler friendAssembler = friendInformationService.getFriends(userList[0].getId(), Long.valueOf(-1));
        assertThat(friendAssembler.friends().size()).isLessThanOrEqualTo(FRIENDS_PAGE_SIZE);
        for (int i = 0; i < friendAssembler.friends().size(); i++) {
            FriendInformation friend = friendAssembler.friends().get(i);
            User user = userList[i + 1];

            assertAll(
                    () -> assertThat(friend.getId()).isEqualTo(user.getId()),
                    () -> assertThat(friend.getName()).isEqualTo(user.getName()),
                    () -> assertThat(friend.getStoreName()).isEqualTo(user.getManagerStore().getName()),
                    () -> assertThat(friend.getRelationStatus()).isEqualTo(RelationStatus.ACCEPT.getValue())
            );
        }
    }

    @Test
    @DisplayName("User ID에 대하여 해당 유저가 요청한 친구 리스트를 조회한다 (lastUserId 이후 + LastModifiedDate 최신순)")
    void getWaitingFriends() {
        // given
        for (int i = 9; i >= 1; i--) {
            friendRepository.save(Friend.createFriend(userList[0], userList[i], RelationStatus.REQUEST));
        }

        // when - then
        FriendAssembler friendAssembler = friendInformationService.getWaitingFriends(userList[0].getId(), Long.valueOf(-1));
        assertThat(friendAssembler.friends().size()).isLessThanOrEqualTo(WAITING_FRIENDS_PAGE_SIZE);
        for (int i = 0; i < friendAssembler.friends().size(); i++) {
            FriendInformation friend = friendAssembler.friends().get(i);
            User user = userList[i + 1];

            assertAll(
                    () -> assertThat(friend.getId()).isEqualTo(user.getId()),
                    () -> assertThat(friend.getName()).isEqualTo(user.getName()),
                    () -> assertThat(friend.getStoreName()).isEqualTo(user.getManagerStore().getName()),
                    () -> assertThat(friend.getRelationStatus()).isEqualTo(RelationStatus.REQUEST.getValue())
            );
        }
    }

    @Test
    @DisplayName("User ID에 대하여 해당 유저에게 요청한 친구 리스트를 조회한다 (lastUserId 이후 + LastModifiedDate 최신순)")
    void getRequestedFriends() {
        // given
        for (int i = 2; i >= 1; i--) {
            friendRepository.save(Friend.createFriend(userList[i], userList[0], RelationStatus.REQUEST));
        }

        // when - then
        FriendAssembler friendAssembler = friendInformationService.getRequestedFriends(userList[0].getId(), Long.valueOf(-1));
        assertThat(friendAssembler.friends().size()).isLessThanOrEqualTo(REQUESTED_FRIENDS_PAGE_SIZE);
        for (int i = 0; i < friendAssembler.friends().size(); i++) {
            FriendInformation friend = friendAssembler.friends().get(i);
            User user = userList[i + 1];

            assertAll(
                    () -> assertThat(friend.getId()).isEqualTo(user.getId()),
                    () -> assertThat(friend.getName()).isEqualTo(user.getName()),
                    () -> assertThat(friend.getStoreName()).isEqualTo(user.getManagerStore().getName()),
                    () -> assertThat(friend.getRelationStatus()).isEqualTo(RelationStatus.REQUEST.getValue())
            );
        }
    }
}