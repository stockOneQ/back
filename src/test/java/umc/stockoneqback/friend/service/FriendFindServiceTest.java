package umc.stockoneqback.friend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.friend.service.dto.SearchUserResponse;
import umc.stockoneqback.global.base.BaseException;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.UserFindService;
import umc.stockoneqback.user.service.UserService;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static umc.stockoneqback.fixture.UserFixture.*;

@DisplayName("Friend [Service Layer] -> FriendFindService 테스트")
class FriendFindServiceTest extends ServiceTest {
    @Autowired
    protected FriendService friendService;

    @Autowired
    protected FriendFindService friendFindService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserFindService userFindService;

    private Long searcherId;
    private Long searchedUserId;
    private Long partTimerUserId;

    @BeforeEach
    void setUp() {
        Long storeId1 = storeRepository.save(createStore("스타벅스 - 광화문점", "카페", "ABC123", "서울시 종로구")).getId();
        Long storeId2 = storeRepository.save(createStore("이디야 - 잠실점", "카페", "ABC1234", "서울시 송파구")).getId();

        searcherId = userService.saveManager(UNKNOWN.toUser(), storeId1);
        searchedUserId = userService.saveManager(ANNE.toUser(), storeId2);
        partTimerUserId = userService.savePartTimer(SAEWOO.toUser(), "스타벅스 - 광화문점", "ABC123");
    }

    @Nested
    @DisplayName("점주님 검색 - Friend Search")
    class searchFriends {
        @Test
        @DisplayName("아르바이트생이라면 점주 검색에 실패한다")
        void throwIsAPartTimer(){
            // when - then
            assertThatThrownBy(() -> friendFindService.searchFriends(partTimerUserId, "앤"))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_IS_NOT_ALLOWED_TO_SEARCH.getMessage());
        }

        @Test
        @DisplayName("점주 검색에 성공한다")
        void success() throws IOException {
            // given
            List<SearchUserResponse> result = friendFindService.searchFriends(searcherId, "앤");

            // when
            User searchedUser = userFindService.findById(searchedUserId);

            // then
            assertThat(result.get(0).name()).isEqualTo(searchedUser.getName());
        }
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