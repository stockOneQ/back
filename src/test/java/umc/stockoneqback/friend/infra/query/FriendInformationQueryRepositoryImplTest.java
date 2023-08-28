package umc.stockoneqback.friend.infra.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.friend.domain.Friend;
import umc.stockoneqback.friend.infra.query.dto.response.FriendInformation;
import umc.stockoneqback.friend.domain.FriendRepository;
import umc.stockoneqback.global.base.RelationStatus;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.*;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Friend [Repository Layer] -> FriendInformationQueryRepository 테스트")
class FriendInformationQueryRepositoryImplTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FriendRepository friendRepository;

    private final Store[] storeList = new Store[10];
    private final User[] userList = new User[12];

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
            storeList[i].updateManager(userList[i]);
        }
    }

    @Test
    @DisplayName("유저 ID 및 Friend 상태를 통해서 유저가 친구를 신청한 유저의 리스트를 조회한다 (LastModified 최신순 정렬)")
    void findReceiversByUserIdAndRelationStatus() {
        // given
        for (int i = 9; i >= 1; i--) {
            friendRepository.save(Friend.createFriend(userList[0], userList[i], RelationStatus.REQUEST));
        }

        // when - then
        List<FriendInformation> receivers = friendRepository.findReceiversByUserIdAndRelationStatus(userList[0].getId(), RelationStatus.REQUEST);

        for (int i = 0; i < receivers.size(); i++) {
            FriendInformation receiver = receivers.get(i);
            User user = userList[i + 1];

            assertAll(
                    () -> assertThat(receiver.getId()).isEqualTo(user.getId()),
                    () -> assertThat(receiver.getName()).isEqualTo(user.getName()),
                    () -> assertThat(receiver.getStoreName()).isEqualTo(user.getManagerStore().getName()),
                    () -> assertThat(receiver.getRelationStatus()).isEqualTo(RelationStatus.REQUEST.getValue())
            );
        }
    }

    @Test
    @DisplayName("유저 ID 및 Friend 상태를 통해서 유저에게 친구를 신청한 유저의 리스트를 조회한다 (LastModified 최신순 정렬)")
    void findSendersByUserIdAndRelationStatus() {
        // given
        for (int i = 9; i >= 1; i--) {
            friendRepository.save(Friend.createFriend(userList[i], userList[0], RelationStatus.REQUEST));
        }

        // when - then
        List<FriendInformation> senders = friendRepository.findSendersByUserIdAndRelationStatus(userList[0].getId(), RelationStatus.REQUEST);

        for (int i = 0; i < senders.size(); i++) {
            FriendInformation sender = senders.get(i);
            User user = userList[i + 1];

            assertAll(
                    () -> assertThat(sender.getId()).isEqualTo(user.getId()),
                    () -> assertThat(sender.getName()).isEqualTo(user.getName()),
                    () -> assertThat(sender.getStoreName()).isEqualTo(user.getManagerStore().getName()),
                    () -> assertThat(sender.getRelationStatus()).isEqualTo(RelationStatus.REQUEST.getValue())
            );
        }
    }
}