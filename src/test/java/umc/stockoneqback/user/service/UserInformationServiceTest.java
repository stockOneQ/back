package umc.stockoneqback.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import umc.stockoneqback.common.ServiceTest;
import umc.stockoneqback.fixture.StoreFixture;
import umc.stockoneqback.global.exception.BaseException;
import umc.stockoneqback.role.domain.store.Store;
import umc.stockoneqback.user.domain.Email;
import umc.stockoneqback.user.domain.User;
import umc.stockoneqback.user.exception.UserErrorCode;
import umc.stockoneqback.user.service.dto.response.LoginIdResponse;
import umc.stockoneqback.user.service.dto.response.UserInformationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static umc.stockoneqback.fixture.UserFixture.MIKE;

@DisplayName("User [Service Layer] -> UserInformationService 테스트")
class UserInformationServiceTest extends ServiceTest {
    @Autowired
    private UserInformationService userInformationService;

    private User user;

    @BeforeEach
    void setUp() {
        Store store = storeRepository.save(StoreFixture.A_PASTA.toStore());
        user = userRepository.save(MIKE.toUser());
        user.registerManagerStore(store);
    }

    @Nested
    @DisplayName("로그인 아이디 찾기")
    class findLoginId {
        @Test
        @DisplayName("정보가 일치하는 사용자를 찾을 수 없으면 로그인 아이디를 찾을 수 없다")
        void throwExceptionByUserNotFound() {
            // when - then
            assertThatThrownBy(() -> userInformationService.findLoginId(MIKE.getName() + "diff", MIKE.getBirth(), Email.from(MIKE.getEmail())))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("로그인 아이디 찾기에 성공한다")
        void success() {
            // when
            LoginIdResponse response = userInformationService.findLoginId(MIKE.getName(), MIKE.getBirth(), Email.from(MIKE.getEmail()));

            // then
            assertThat(response.loginId()).isEqualTo(MIKE.getLoginId());
        }
    }

    @Nested
    @DisplayName("사용자 정보 조회")
    class getInformation {
        @Test
        @DisplayName("정보가 일치하는 사용자를 찾을 수 없으면 사용자 정보 조회를 할 수 없다")
        void throwExceptionByUserNotFound() {
            // when - then
            assertThatThrownBy(() -> userInformationService.getInformation(user.getId() + 100L))
                    .isInstanceOf(BaseException.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("사용자 정보 조회에 성공한다")
        void success() {
            // when
            UserInformationResponse response = userInformationService.getInformation(user.getId());

            // then
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(user.getId()),
                    () -> assertThat(response.email()).isEqualTo(user.getEmail().getValue()),
                    () -> assertThat(response.loginId()).isEqualTo(user.getLoginId()),
                    () -> assertThat(response.name()).isEqualTo(user.getName()),
                    () -> assertThat(response.birth()).isEqualTo(user.getBirth()),
                    () -> assertThat(response.phoneNumber()).isEqualTo(user.getPhoneNumber()),
                    () -> assertThat(response.role()).isEqualTo(user.getRole().getAuthority()),
                    () -> assertThat(response.storeName()).isEqualTo(user.getManagerStore().getName()),
                    () -> assertThat(response.storeCode()).isEqualTo(user.getManagerStore().getCode()),
                    () -> assertThat(response.storeAddress()).isEqualTo(user.getManagerStore().getAddress()),
                    () -> assertThat(response.companyName()).isNull()
            );
        }
    }
}