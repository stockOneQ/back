package umc.stockoneqback.user.infra.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.RepositoryTest;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.role.domain.store.StoreRepository;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.domain.UserRepository;
import umc.stockoneqback.user.domain.search.UserSearchType;
import umc.stockoneqback.user.infra.query.dto.FindManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.StoreFixture.*;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Friend [Repository Layer] -> FriendFindQueryRepository 테스트")
class UserFindQueryRepositoryImplTest extends RepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    private final Store[] storeList = new Store[5];
    private final User[] userList = new User[9];
    private final UserSearchType SEARCH_TYPE_NAME = UserSearchType.NAME;
    private final UserSearchType SEARCH_TYPE_STORE = UserSearchType.STORE;
    private final UserSearchType SEARCH_TYPE_ADDRESS = UserSearchType.ADDRESS;
    private final static String SEARCH_NAME = "아";
    private final static String SEARCH_STORE = "과일";
    private final static String SEARCH_ADDRESS = "경기도";

    @BeforeEach
    void setUp() {
        userList[0] = userRepository.save(ANNE.toUser());
        userList[1] = userRepository.save(ELLA.toUser());
        userList[2] = userRepository.save(MIKE.toUser());
        userList[3] = userRepository.save(SOPHIA.toUser());
        userList[4] = userRepository.save(UNKNOWN.toUser());
        userList[5] = userRepository.save(SAEWOO.toUser());
        userList[6] = userRepository.save(WIZ.toUser());
        userList[7] = userRepository.save(WONI.toUser());
        userList[8] = userRepository.save(OLIVIA.toUser());

        storeList[0] = storeRepository.save(Z_YEONGTONG.toStore(userList[0]));
        storeList[1] = storeRepository.save(Z_SIHEUNG.toStore(userList[1]));
        storeList[2] = storeRepository.save(Y_YEONGTONG.toStore(userList[2]));
        storeList[3] = storeRepository.save(A_PASTA.toStore(userList[3]));
        storeList[4] = storeRepository.save(B_CHICKEN.toStore(userList[4]));

        userList[0].registerManagerStore(storeList[0]);
        userList[1].registerManagerStore(storeList[1]);
        userList[2].registerManagerStore(storeList[2]);
        userList[3].registerManagerStore(storeList[3]);
        userList[4].registerManagerStore(storeList[4]);

        storeList[0].updatePartTimer(userList[5]);
        storeList[1].updatePartTimer(userList[7]);
    }

    @Test
    @DisplayName("주어진 값이 이름에 포함되어 있는 매니저인 유저를 검색한다")
    void findManagersBySearchTypeName() {
        // when - then
        List<FindManager> findManagers = userRepository.findManagersBySearchType(userList[0].getId(), SEARCH_TYPE_NAME, SEARCH_NAME);

        System.out.println("************");
        System.out.println(findManagers);
        System.out.println("************");

        assertAll(
                () -> assertThat(findManagers.size()).isEqualTo(1),
                () -> assertThat(findManagers.get(0).getName()).isEqualTo(userList[3].getName()),
                () -> assertThat(findManagers.get(0).getId()).isEqualTo(userList[3].getId()),
                () -> assertThat(findManagers.get(0).getStoreName()).isEqualTo(userList[3].getManagerStore().getName()),
                () -> assertThat(findManagers.get(0).getPhoneNumber()).isEqualTo(userList[3].getPhoneNumber())
        );
    }

    @Test
    @DisplayName("주어진 값이 가게 이름(상호명)에 포함되어 있는 매니저인 유저를 검색한다")
    void findManagersBySearchTypeStore() {
        // when - then
        List<FindManager> findManagers = userRepository.findManagersBySearchType(userList[4].getId(), SEARCH_TYPE_STORE, SEARCH_STORE);

        for (int i = 0; i < findManagers.size(); i++) {
            FindManager findManager = findManagers.get(i);
            User user = userList[i];

            assertAll(
                    () -> assertThat(findManager.getName()).isEqualTo(user.getName()),
                    () -> assertThat(findManager.getId()).isEqualTo(user.getId()),
                    () -> assertThat(findManager.getStoreName()).isEqualTo(user.getManagerStore().getName()),
                    () -> assertThat(findManager.getPhoneNumber()).isEqualTo(user.getPhoneNumber())
            );
        }
    }

    @Test
    @DisplayName("주어진 값이 지역명에 포함되어 있는 매니저인 유저를 검색한다")
    void findManagersBySearchTypeAddress() {
        // when - then
        List<FindManager> findManagers = userRepository.findManagersBySearchType(userList[4].getId(), SEARCH_TYPE_ADDRESS, SEARCH_ADDRESS);

        for (int i = 0; i < findManagers.size(); i++) {
            FindManager findManager = findManagers.get(i);
            User user = userList[i];

            assertAll(
                    () -> assertThat(findManager.getName()).isEqualTo(user.getName()),
                    () -> assertThat(findManager.getId()).isEqualTo(user.getId()),
                    () -> assertThat(findManager.getStoreName()).isEqualTo(user.getManagerStore().getName()),
                    () -> assertThat(findManager.getPhoneNumber()).isEqualTo(user.getPhoneNumber())
            );
        }
    }
}